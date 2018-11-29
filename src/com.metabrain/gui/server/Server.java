package com.metabrain.gui.server;


import com.google.gson.Gson;
import com.metabrain.gui.server.model.GetNodeBody;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Server extends NanoHTTPD {

    static private Gson json = new Gson();
    private DjsThread thread = new DjsThread();

    public Server(int port) {
        super(port);
    }

    public void join() throws InterruptedException {
        myThread.join();
    }

    @Override
    public Response serve(IHTTPSession session) {

        String responseData = null;
        try {
            URI uri = new URI(session.getUri());

            if (session.getMethod() == NanoHTTPD.Method.POST) {

                Map<String, String> files = new HashMap<>();
                session.parseBody(files);
                String body = files.get("postData");
                GetNodeBody getNodeBody;
                switch (uri.getPath()) {
                    case "/setNode":
                        getNodeBody = json.fromJson(body, GetNodeBody.class);
                        responseData = thread.setNode(getNodeBody);
                        break;
                    case "/getNode":
                        getNodeBody = json.fromJson(body, GetNodeBody.class);
                        responseData = thread.getNode(getNodeBody);
                        break;
                    case "/runNode":
                        getNodeBody = json.fromJson(body, GetNodeBody.class);
                        responseData = thread.runNode(getNodeBody);
                        break;
                    case "/stop":
                        System.exit(0);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        NanoHTTPD.Response response;
        if (responseData != null){
            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, responseData);
        }else{
            response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "empty");
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        return response;
    }


}
