package com.metabrain.gui.server;


import com.google.gson.Gson;
import com.metabrain.djs.refactored.Formatter;
import com.metabrain.djs.refactored.node.Node;
import com.metabrain.gui.server.model.GetNodeBody;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Server extends NanoHTTPD {

    static private Gson json = new Gson();
    private DjsThread thread = new DjsThread();
    private static final String APPLICATION_JSON = "application/json";

    public Server(int port) {
        super(port);
    }

    public void join() throws InterruptedException {
        myThread.join();
    }

    @Override
    public Response serve(IHTTPSession session) {

        String responseString = null;
        try {
            URI uri = new URI(session.getUri());

            if (session.getMethod() == NanoHTTPD.Method.POST) {

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
                        if (request.run != null && request.run)
                            thread.runNode(request);
                        Node node = thread.getNode(request.nodeLink, request.replacements);
                        request.nodes = Formatter.toMap(node);
                        responseString = json.toJson(request);
                        break;
                    case "stop":
                        System.exit(0);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        NanoHTTPD.Response response =
                NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, APPLICATION_JSON, responseString);
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        return response;
    }


}
