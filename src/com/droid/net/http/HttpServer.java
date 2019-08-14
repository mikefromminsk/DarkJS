package com.droid.net.http;


import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Data;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.serialization.json.JsonSerializer;
import com.droid.djs.serialization.node.NodeSerializer;
import com.droid.djs.treads.Threads;
import com.droid.gdb.map.Crc16;
import com.droid.instance.Instance;
import org.nanohttpd.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidParameterException;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpServer extends NanoHTTPD {

    public static final String FORM_DATA = "application/x-www-form-urlencoded";
    public static int defaultPort = 8080;
    public static String BASIC_AUTH_PREFIX = "Basic ";

    public HttpServer() {
        super(defaultPort + Instance.get().instanceID);
    }

    @Override
    public Response serve(IHTTPSession session) {
        super.serve(session);
        long startRequestTime = new Date().getTime();
        Response response = null;
        Instance.connectThreadByPortAdditional(getListeningPort() - defaultPort);
        try {
            String requestContentType = session.getHeaders().get(Headers.CONTENT_TYPE);
            if (requestContentType != null)
                requestContentType = requestContentType.toLowerCase();
            if (session.getMethod() == Method.GET
                    || session.getMethod() == Method.POST && FORM_DATA.equals(requestContentType)) {
                //Authorization: Basic userid:password
                String authorization = session.getHeaders().get(Headers.AUTHORIZATION);
                if (authorization == null || !authorization.startsWith(BASIC_AUTH_PREFIX)) {
                    response = NanoHTTPD.newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, "Need basic auth");
                    response.addHeader(Headers.AUTHENTICATE, "Basic realm=\"Access to the site\"");
                } else {
                    Node node = null;
                    try {
                        node = Files.getNodeIfExist(session.getUri());
                    } catch (Exception e) {
                        System.out.println("uri error with: " + session.getUri());
                        e.printStackTrace();
                    }

                    if (node == null) {
                        response = newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "File not Found");
                    } else {
                        authorization = authorization.substring(BASIC_AUTH_PREFIX.length());
                        authorization = new String(Base64.getDecoder().decode(authorization.getBytes()));
                        String login = authorization.substring(0, authorization.indexOf(":"));
                        String password = authorization.substring(authorization.indexOf(":") + 1);
                        Long access_token = (long) Crc16.getHash(login + password);

                        node = Files.getNode(session.getUri(), null, access_token);

                        if (node == null) {
                            response = newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "Access denied");
                        } else {
                            Map<String, String> args = parseArguments(session.getQueryParameterString());

                            NodeBuilder builder = new NodeBuilder().set(node);

                            for (Node param : builder.getParams())
                                builder.set(param).setValue(null).commit();

                            for (String argsKey : args.keySet())
                                setParam(node, argsKey, args.get(argsKey));

                            Instance.get().getThreads().run(node, null, false, access_token);

                            builder.set(node);
                            if (builder.isFunction())
                                builder.set(builder.getValueNode());

                            ResponseWithType responseData = getResponse(builder);
                            ByteArrayInputStream dataStream = new ByteArrayInputStream(responseData.data);
                            response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, responseData.type, dataStream, responseData.data.length);
                            response.addHeader("content-length", "" + responseData.data.length); // fix nanohttpd issue when content type is define
                        }
                    }
                }
            } else if (session.getMethod() == Method.POST) {
                Files.putFile(session.getUri(), session.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
        } finally {
            if (response != null) {
                response.addHeader("Access-Control-Allow-Origin", "*");
                response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            }
        }

        Instance.disconnectThread();

        if (response == null)
            response = NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "response is empty");

        System.out.println(session.getUri() + " (" + (new Date().getTime() - startRequestTime) + ")");

        return response;
    }

    String convertExtensionToMimeType(String extension) {
        switch (extension) {
            case "js":
                return "text/javascript";
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "png":
                return "image/png";
            default:
                return null;
        }
    }

    private ResponseWithType getResponse(NodeBuilder builder) {
        if (builder.getNode() == null)
            return new ResponseWithType("application/json", "null");

        String parser = builder.getParserString();
        if (parser != null)
            switch (parser) {
                case "json":
                    return new ResponseWithType("application/json", JsonSerializer.serialize(builder));
                case "node.js":
                    return new ResponseWithType("application/json", NodeSerializer.toJson(builder.getNode()));
                default: // node is static file
                    if (builder.getValueNode() instanceof Data)
                        return new ResponseWithType(convertExtensionToMimeType(parser), ((Data) builder.getValueNode()).data.readBytes());
                    else
                        return new ResponseWithType(convertExtensionToMimeType(parser), "");
            }

        return new ResponseWithType("application/json", NodeSerializer.toJson(builder.getNode()));
    }

    Map<String, String> parseArguments(String args) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        if (args != null && !args.equals("")) {
            try {
                for (String pair : args.split("&")) {
                    int idx = pair.indexOf("=");
                    query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
            }
        }
        return query_pairs;
    }

    void setParam(Node node, String key, String value) {
        NodeBuilder builder = new NodeBuilder();
        Node param = null;
        // TODO key can be par[title][0]
        for (Node paramNode : builder.set(node).getParams())
            if (key.equals(builder.set(paramNode).getTitleString())) {
                param = paramNode;
                break;
            }
        if (param != null) {
            Node valueNode;
            if (value.charAt(0) == '!') {
                valueNode = builder.createString(value.substring(1));
            } else if (value.charAt(0) >= '0' && value.charAt(0) <= '9') {
                try {
                    Double numberValue = Double.valueOf(value);
                    valueNode = builder.createNumber(numberValue);
                } catch (NumberFormatException e) {
                    throw new InvalidParameterException();
                }
            } else if ("true".equals(value)) {
                valueNode = builder.createBool(true);
            } else if ("false".equals(value)) {
                valueNode = builder.createBool(false);
            } else
                throw new InvalidParameterException();

            builder.set(param).setValue(valueNode).commit();
        }
    }
}
