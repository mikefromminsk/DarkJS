package org.pdk.net.http;


import org.pdk.engine.fs.Files;
import org.pdk.engine.store.nodes.Node;
import org.pdk.engine.store.nodes.NodeBuilder;
import org.pdk.engine.consts.NodeType;
import org.pdk.engine.convertors.node.HttpResponse;
import org.pdk.engine.convertors.node.HttpResponseType;
import org.pdk.engine.convertors.node.NodeParser;
import org.pdk.engine.convertors.node.NodeSerializer;
import org.pdk.gdb.map.Crc16;
import org.pdk.instance.Instance;
import com.sun.istack.internal.NotNull;
import org.nanohttpd.NanoHTTPD;

import java.io.*;
import java.net.*;
import java.security.InvalidParameterException;
import java.util.*;

public class HttpClientServer extends NanoHTTPD {

    public static class Headers {
        public final static String CONTENT_LENGTH = "content-length";
        public final static String CONTENT_TYPE = "content-type";
        public static final String AUTHORIZATION = "authorization";
        public static final String DESTINATION_NODENAME = "destination-nodename";
        public static final String AUTHENTICATE = "WWW-Authenticate";
        public static final String NODENAME = "nodename";
        public static final String NODEPORT = "nodeport";
    }

    public static int defaultPort = 8080;
    public static String BASIC_AUTH_PREFIX = "Basic ";
    public static Map<String, Host> nodeNames = new HashMap<>();

    class Host {
        String ip;
        int port;

        public Host(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }

    public HttpClientServer(Integer port) throws IOException {
        super(port == null ? defaultPort : port);
        start(0);
        if (Instance.get().proxyHost != null)
            try {
                request(Instance.get().proxyHost, Instance.get().proxyPortAdding + defaultPort, "/", "", new HashMap<>()).getInputStream().close();
            } catch (Exception ignored) {
            }
    }

    Long authToToken(String authorization) {
        if (authorization.startsWith(BASIC_AUTH_PREFIX)) {
            authorization = authorization.substring(BASIC_AUTH_PREFIX.length());
            authorization = new String(Base64.getDecoder().decode(authorization.getBytes()));
            String login = authorization.substring(0, authorization.indexOf(":"));
            String password = authorization.substring(authorization.indexOf(":") + 1);
            return Crc16.getHash(login + password);
        }
        return null;
    }

    @Override
    public Response serve(IHTTPSession sessionObject) {
        super.serve(sessionObject);
        long startRequestTime = new Date().getTime();
        HTTPSession session = (HTTPSession) sessionObject;
        Response response = null;
        Instance.connectThreadByPortAdditional(getListeningPort() - defaultPort);
        try {
            String requestContentType = session.getHeaders().get(Headers.CONTENT_TYPE);
            if (requestContentType != null)
                requestContentType = requestContentType.toLowerCase();
            if (session.getMethod() == Method.GET
                    || session.getMethod() == Method.POST && (requestContentType != null && requestContentType.equals("application/x-www-form-urlencoded"))) {
                String nodename = session.getHeaders().get(Headers.NODENAME);
                // TODO add remove conditions
                if (nodename != null) {
                    String nodeport = session.getHeaders().get(Headers.NODEPORT);
                    nodeNames.put(nodename, new Host(session.remoteIp, Integer.valueOf(nodeport)));
                }

                String path = session.getUri();
                if (path.equals("") || path.equals("/"))
                    path = "index";

                String destinationNodename = session.getHeaders().get(Headers.DESTINATION_NODENAME);
                if (destinationNodename != null) {
                    Host host = nodeNames.get(destinationNodename);
                    if (host == null) {
                        throw new NullPointerException();
                    } else {
                        Map<String, String> headers = session.getHeaders();
                        headers.remove(Headers.NODENAME);
                        headers.remove(Headers.NODEPORT);
                        headers.remove(Headers.DESTINATION_NODENAME);
                        HttpURLConnection conn = request(host.ip, host.port, path, session.getQueryParameterString(), headers);
                        InputStream inputStream = conn.getInputStream();
                        int contentLength = Integer.parseInt(conn.getHeaderField(Headers.CONTENT_LENGTH));
                        String contentType = conn.getHeaderField(Headers.CONTENT_TYPE);

                        response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, contentType, inputStream, contentLength);
                        response.addHeader(Headers.CONTENT_LENGTH, "" + contentLength);
                    }
                } else {
                    String authorization = session.getHeaders().get(Headers.AUTHORIZATION);
                    if (authorization == null || !authorization.startsWith(BASIC_AUTH_PREFIX)) {
                        response = NanoHTTPD.newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, "Need basic auth");
                        response.addHeader(Headers.AUTHENTICATE, "Basic realm=\"Access to the site\"");
                    } else {

                        Long accessToken = authToToken(authorization);

                        Node node = Files.getNodeIfExist(path, accessToken);

                        if (node == null) {
                            response = newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "File not Found");
                        } else {
                            Map<String, String> args = parseArguments(session.getQueryParameterString());

                            NodeBuilder builder = new NodeBuilder().set(node);

                            for (Node param : builder.getParams())
                                builder.set(param).setValue(null).commit();

                            for (String argsKey : args.keySet())
                                setParam(node, argsKey, args.get(argsKey));

                            Instance.get().getThreads().run(node, null, false, accessToken);

                            if (builder.set(node).isFunction())
                                builder.set(builder.getValueNode());

                            HttpResponse responseData = NodeSerializer.getResponse(builder.getNode());
                            ByteArrayInputStream dataStream = new ByteArrayInputStream(responseData.data);
                            response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, responseData.type, dataStream, responseData.data.length);
                            response.addHeader(Headers.CONTENT_LENGTH, "" + responseData.data.length); // fix nanohttpd issue when content type is define
                        }
                    }
                }

            } else if (session.getMethod() == Method.POST) {
                Files.putFile(session.getUri(), session.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
        }

        Instance.disconnectThread();

        if (response == null)
            response = NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "response is empty");

        System.out.println(session.getUri() + " (" + (new Date().getTime() - startRequestTime) + ")");

        return response;
    }

    Map<String, String> parseArguments(String args) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        if (args != null && !args.equals("")) {
            try {
                for (String pair : args.split("&")) {
                    int idx = pair.indexOf("=");
                    if (idx != -1) {
                        query_pairs.put(pair.substring(0, idx), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    } else {
                        query_pairs.put("[0]", URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    }
                }
            } catch (UnsupportedEncodingException e) {
            }
        }
        return query_pairs;
    }

    void setParam(Node node, String key, String value) {
        NodeBuilder builder = new NodeBuilder();
        Node param = null;

        if (key.charAt(0) == '[') {
            // TODO test key = "[key][0][key][0]"
            param = builder.set(node).getParamNode(0);
        } else {
            for (Node paramNode : builder.set(node).getParams())
                if (key.equals(builder.set(paramNode).getTitleString())) {
                    param = paramNode;
                    break;
                }
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

    public static String serializeParameters(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(entry.getKey());
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
    }

    Map<String, String> buildParameters(Node paramter) {
        NodeBuilder builder = new NodeBuilder();
        Map<String, String> result = new HashMap<>();

        if (builder.set(paramter).isDataVariable()) {
            builder.set(builder.getValueNode());
        }

        if (builder.isString()) {
            result.put("[0]", "!" + builder.getData().readString());
        } else if (builder.isNumber()) {
            result.put("[0]", builder.getData().readString());
        } else if (builder.isBoolean()) {
            result.put("[0]", builder.getData().readString());
        } else /*if (builder.isNULL()){
            result.put("[0]", "!" + builder.getData().readString());
        }else*/ if (builder.set(paramter).isObject()) {
            // TODO test with locals more than 2 levels
            for (Node local : builder.getLocalNodes()) {
                HttpResponse response = NodeSerializer.getResponse(builder.getNode());
                String key = URLEncoder.encode(builder.set(local).getTitleString());
                result.put(key, new String(response.data));
            }
        } else if (builder.set(paramter).isArray()) {
            // TODO add code
        }
        return result;
    }

    // TODO change Map<String, String> parameters to Map<String, InputStream> parameters
    public Node requestToProxy(String nodename, String path, Node parameter) throws IOException {

        String authStr = Instance.get().login + ":" + Instance.get().password;
        String authEncodeStr = Base64.getEncoder().encodeToString(authStr.getBytes());
        Map<String, String> headers = new HashMap<>();
        headers.put(Headers.AUTHORIZATION, BASIC_AUTH_PREFIX + authEncodeStr);
        headers.put(Headers.DESTINATION_NODENAME, nodename);

        String serializeNode = serializeParameters(buildParameters(parameter));

        HttpURLConnection conn = request(Instance.get().proxyHost, Instance.get().proxyPortAdding + defaultPort, path, serializeNode, headers);

        InputStream inputStream = conn.getInputStream();
        String contentType = conn.getHeaderField(Headers.CONTENT_TYPE);
        NodeBuilder builder = new NodeBuilder();
        switch (contentType) {
            case HttpResponseType.TEXT:
                return builder.create(NodeType.STRING).setData(inputStream).commit();
            case HttpResponseType.NUMBER_BASE10:
                return builder.create(NodeType.NUMBER).setData(inputStream).commit();
            case HttpResponseType.BOOLEAN:
                return builder.create(NodeType.BOOLEAN).setData(inputStream).commit();
            case HttpResponseType.NULL:
                return null;
            case HttpResponseType.JSON:
                return NodeParser.fromStream(inputStream);
        }
        // TODO case when http type is not support
        return null;
    }

    public HttpURLConnection request(@NotNull String host, @NotNull int port, @NotNull String path, @NotNull String data, @NotNull Map<String, String> headers) throws IOException {
        URL url = new URL("http", host, port, path);
        System.out.println("GET " + url.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        for (String key : headers.keySet())
            conn.addRequestProperty(key, headers.get(key));
        conn.addRequestProperty(Headers.CONTENT_LENGTH, "" + data.length());
        conn.addRequestProperty(Headers.NODENAME, Instance.get().nodename);
        conn.addRequestProperty(Headers.NODEPORT, "" + (Instance.get().portAdding + defaultPort));

        conn.setDoOutput(true);
        OutputStream outputStream = conn.getOutputStream();
        DataOutputStream out = new DataOutputStream(outputStream);
        out.writeBytes(data);
        out.flush();
        out.close();

        return conn;
    }
}
