package com.metabrain.gui.server;


import com.google.gson.Gson;
import com.metabrain.djs.refactored.Runner;
import com.metabrain.gui.server.model.ToastData;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Server extends NanoHTTPD {

    private static Gson json = new Gson();
    private Map<Long, Thread> activeThreads = new LinkedHashMap<>();

    public Server(int port) {
        super(port);
    }

    void startThread(Long threadNodeId){
        if (activeThreads.get(threadNodeId) == null) {
            Thread thread = new Thread(new Runner());
            thread.start();
            activeThreads.put(threadNodeId, thread);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            URI uri = new URI(session.getUri());

            if (session.getMethod() == NanoHTTPD.Method.POST){

                Map<String, String> files = new HashMap<>();
                session.parseBody(files);
                String body = session.getQueryParameterString();

                switch (uri.getPath()){
                    case "wef":
                        ToastData toastData = json.fromJson(body, ToastData.class);
                        startThread(toastData.toastID);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "hello world");
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }


}
