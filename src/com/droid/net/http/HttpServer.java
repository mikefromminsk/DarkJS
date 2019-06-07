package com.droid.net.http;


import com.droid.djs.node.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpServer extends NanoHTTPD {

    static private Gson json = new GsonBuilder().setPrettyPrinting().create();

    public static int defaultPort = 80;
    public static int debugPort = 8080;

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
            String requestContentType = session.getHeaders().get(HttpHeader.CONTENT_TYPE);
            if (requestContentType != null)
                requestContentType = requestContentType.toLowerCase();
            if (session.getMethod() == Method.GET
                    || session.getMethod() == Method.POST && ContentType.FORM_DATA.equals(requestContentType)) {
                // run
                Map<String, String> args = null;
                if (session.getMethod() == Method.POST) {
                    args = parseArguments(session.getInputStream());
                } else if (session.getMethod() == Method.GET) {
                    args = parseArguments(session.getQueryParameterString());
                }
                Node node = null;
                for (String nodePath : getFileNames(session.getUri())) {
                    node = NodeUtils.getNode(nodePath, false);
                    if (node != null)
                        break;
                }
                if (node == null) {
                    response = newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not Found");
                } else {
                    ArrayList<String> argsKeys = new ArrayList<>(args.keySet());
                    for (String argsKey : argsKeys)
                        setParam(node, argsKey, args.get(argsKey));

                    DataInputStream resultStream = (DataInputStream) getResult(node);
                    String responseContentType = ContentType.getContentTypeFromName(new NodeBuilder().set(node).getTitleString());

                    response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, responseContentType, resultStream, resultStream.length());
                }
            } else if (session.getMethod() == Method.POST) {
                NodeUtils.putFile(session.getUri(), session.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
        } finally {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        }
        return response;
    }

    private String[] getFileNames(String filename) {
        return new String[]{filename};
    }

    private InputStream getResult(Node resultNode) {
        NodeBuilder builder = new NodeBuilder();
        Node nodeValue = builder.set(resultNode).getValueOrSelf();
        return builder.set(nodeValue).getData();
    }

    Map<String, String> parseArguments(String args) {
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

                }
            } else if (param.type == NodeType.OBJECT) {

            }
        }
    }
}
