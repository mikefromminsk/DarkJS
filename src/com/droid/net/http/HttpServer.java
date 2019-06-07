package com.droid.net.http;


import com.droid.djs.Formatter;
import com.droid.djs.node.*;
import com.droid.djs.node.DataInputStream;
import com.droid.net.http.model.GetNodeBody;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;

public class HttpServer extends NanoHTTPD {

    static private Gson json = new GsonBuilder().setPrettyPrinting().create();
    private DjsThread thread = new DjsThread();

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
            String contentType = session.getHeaders().get(HttpHeader.CONTENT_TYPE);
            if (session.getMethod() == Method.GET
                    || session.getMethod() == Method.POST && contentType.equals(ContentType.FORM_DATA)) {
                // run
                Map<String, String> args = null;
                if (session.getMethod() == Method.POST) {
                    args = parseArguments(session.getInputStream());
                } else if (session.getMethod() == Method.GET) {
                    args = parseArguments(session.getQueryParameterString());
                }

                Node node = NodeUtils.getNode(session.getUri());

                ArrayList<String> argsKeys = new ArrayList<>(args.keySet());
                for (int i = 0; i < argsKeys.size(); i++)
                    setParam(node, i, argsKeys.get(i));

                DataInputStream resultStream = (DataInputStream) getResult(node);

                response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, getContentType(node), resultStream, resultStream.length());
            } else if (session.getMethod() == Method.POST) {
                NodeUtils.putFile(session.getUri(), session.getInputStream());
            }

        if (response == null)
            response = NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR,
                    NanoHTTPD.MIME_PLAINTEXT,
                    Response.Status.INTERNAL_ERROR.getDescription());

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        return response;
    }

    private InputStream getResult(Node resultNode) {
        NodeBuilder builder = new NodeBuilder();
        Node nodeValue = builder.set(resultNode).getValueOrSelf();
        return builder.set(nodeValue).getData();
    }

    String getContentType(Node node) {
        return URLConnection.guessContentTypeFromName(new NodeBuilder().set(node).getTitleString());
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

    void setParam(Node node, int index, String value) {
        NodeBuilder builder = new NodeBuilder();
        Node param = builder.set(node).getParamNode(index);
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
