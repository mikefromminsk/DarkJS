package com.metabrain.gui.server;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metabrain.djs.Formatter;
import com.metabrain.djs.node.Node;
import com.metabrain.djs.node.NodeStorage;
import com.metabrain.djs.node.NodeStorageTest;
import com.metabrain.gui.client.WebGuiRoot;
import com.metabrain.gui.server.model.GetNodeBody;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Server extends NanoHTTPD {

    static private Gson json = new GsonBuilder().setPrettyPrinting().create();
    private DjsThread thread = new DjsThread();
    private static final String APPLICATION_JSON = "application/json";

    public Server(int port) {
        super(port);
    }

    public void join() throws InterruptedException {
        myThread.join();
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static String createJsonResponse(Object object) {
        return "{\n" +
                " response: " + json.toJson(object) + "\n" +
                "}";
    }

    @Override
    public Response serve(IHTTPSession session) {
        NanoHTTPD.Response response = null;
        if (session.getMethod() == NanoHTTPD.Method.POST) {
            String responseString = null;
            try {
                URI uri = new URI(session.getUri());

                Map<String, String> files = new HashMap<>();
                session.parseBody(files);
                String body = files.get("postData");
                String path = uri.getPath().substring(1);
                switch (path) {
                    case "node":
                        GetNodeBody request = json.fromJson(body, GetNodeBody.class);
                        request.replacements = new HashMap<>();

                        if (request.nodes != null)
                            thread.updateNode(request);

                        Node node = thread.getNode(request.nodeLink, request.replacements);

                        if (request.source_code != null)
                            thread.parse(node, request);

                        if (request.run != null)
                            thread.runNode(node);

                        request.nodes = Formatter.toMap(node);
                        responseString = json.toJson(request);
                        break;
                    case "testStart":
                        NodeStorageTest.startTest();
                        responseString = createJsonResponse(true);
                        break;
                    case "testProgress":
                        NodeStorageTest.startTest();
                        responseString = createJsonResponse(NodeStorageTest.out);
                        break;
                    case "stop":
                        System.exit(0);
                        break;
                }

            } catch (Exception e) {
                GetNodeBody request = new GetNodeBody();
                request.error = e.getMessage();
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                request.stack = Arrays.asList(errors.toString().split("\r\n\t"));
                responseString = json.toJson(request);
                System.out.println(responseString);
            }
            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, APPLICATION_JSON, responseString);
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");

        } else if (session.getMethod() == NanoHTTPD.Method.GET) {
            try {
                URI uri = new URI(session.getUri());
                String dirPath = WebGuiRoot.class.getPackage().getName().replace('.', '/');
                String fileName = (uri.getPath().equals("/") ? "/index.html" : uri.getPath());
                String filePath = dirPath + fileName;
                InputStream fileStream = getClass().getClassLoader().getResourceAsStream(filePath);
                if (fileStream == null)
                    throw new FileNotFoundException();
                String fileData = convertStreamToString(fileStream);
                String mimeType = URLConnection.guessContentTypeFromName(FilenameUtils.getName(uri.getPath()));
                response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, mimeType, fileData);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                response = NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND,
                        NanoHTTPD.MIME_PLAINTEXT,
                        Response.Status.NOT_FOUND.getDescription());
            }
        }
        if (response == null)
            response = NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR,
                    NanoHTTPD.MIME_PLAINTEXT,
                    Response.Status.INTERNAL_ERROR.getDescription());
        return response;
    }


}
