package com.droid.net.http;


import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.nodes.*;
import com.droid.djs.fs.Files;
import com.droid.djs.serialization.json.JsonSerializer;
import com.droid.djs.serialization.node.NodeSerializer;
import com.droid.djs.treads.Secure;
import com.droid.djs.treads.Threads;
import org.nanohttpd.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpServer extends NanoHTTPD {

    public static final String FORM_DATA = "application/x-www-form-urlencoded";
    public static int defaultPort = 80;
    public static int debugPort = 8080;
    public static String BASIC_AUTH_PREFIX = "Basic ";

    public HttpServer() {
        this(defaultPort);
    }

    public HttpServer(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response response = null;

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
                        node = Files.getNode(session.getUri(), null);
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
                        Long access_token = Secure.getAccessToken(login, password);

                        node = Files.getNode(session.getUri(), null, access_token);

                        if (node == null) {
                            response = newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "Access denied");
                        } else {
                            Map<String, String> args = null;
                            if (session.getMethod() == Method.POST)
                                args = parseArguments(session.getInputStream());
                            else if (session.getMethod() == Method.GET)
                                args = parseArguments(session.getQueryParameterString());

                            ArrayList<String> argsKeys = new ArrayList<>(args.keySet());
                            for (String argsKey : argsKeys)
                                setParam(node, argsKey, args.get(argsKey));

                            Threads.getInstance().run(node, null, false, access_token);

                            NodeBuilder builder = new NodeBuilder().set(node);
                            if (builder.isFunction() && builder.getValueNode() != null)
                                builder.set(builder.getValueNode());

                            String responseData = serializeNode(builder);
                            String mimeType = getContentType(builder);
                            response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, mimeType, responseData);
                            response.addHeader("content-length", "" + responseData.length()); // fix nanohttpd issue when content type is define
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

        if (response == null)
            response = NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "response is empty");

        return response;
    }

    String getContentType(NodeBuilder builder) {
        String parser = builder.getParserString();
        if (parser != null) {
            if (parser.endsWith("json"))
                return "application/json";
            if (parser.endsWith("js"))
                return "text/javascript";
            if (parser.endsWith("html"))
                return "text/html";
            if (parser.endsWith("css"))
                return "text/css";
        }
        if (builder.getValueNode() instanceof Data)
            return "";
        return "application/json";
    }

    private String serializeNode(NodeBuilder builder) {
        String parser = builder.getParserString();
        if (parser != null) {
            if (parser.endsWith("json"))
                return JsonSerializer.serialize(builder);
        }
        if (builder.getValueNode() instanceof Data)
            return ((Data) builder.getValueNode()).data.readString();
        return NodeSerializer.toJson(builder.getNode());
    }

    Map<String, String> parseArguments(String args) {
        if (args == null)
            return new LinkedHashMap<>();
        return parseArguments(new ByteArrayInputStream(args.getBytes()));
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    Map<String, String> parseArguments(InputStream args) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = convertStreamToString(args);
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
            }
        }
        return query_pairs;
    }

    ArrayList toList(String str) {
        return new ArrayList();
    }

    void setParam(Node node, String key, String value) {
        NodeBuilder builder = new NodeBuilder();
        Node param = null;
        for (Node paramNode : builder.set(node).getParams())
            if (key.equals(builder.set(paramNode).getTitleString())) {
                param = paramNode;
                break;
            }
        if (param != null) {
            if (param.type == NodeType.NUMBER) {
                Node number = builder.create(NodeType.NUMBER).setData(value).commit();
                builder.set(param).setValue(number).commit();
            } else if (param.type == NodeType.STRING) {
                Node number = builder.create(NodeType.STRING).setData(value).commit();
                builder.set(param).setValue(number).commit();
            } else if (param.type == NodeType.ARRAY) {
                builder.set(param).clearCells();
                for (Object obj : toList(value)) {
                    //TODO add code
                }
            } else if (param.type == NodeType.OBJECT) {
                //TODO add code
            }
        }
    }
}
