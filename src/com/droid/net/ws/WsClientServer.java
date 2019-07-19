package com.droid.net.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WsClientServer {

    private WebSocketServer server;

    private String nodeName;
    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    private Map<String, WebSocket> inputs = new HashMap<>();
    private Map<String, WebSocketClient> outputs = new HashMap<>();


    public WsClientServer(int port, String nodeName) {
        startServer(port);
        this.nodeName = nodeName;
    }

    void send(String toNodeName, String message) {
        WebSocket serverSocket = inputs.get(toNodeName);
        if (serverSocket != null) {
            serverSocket.send(message);
        } else {
            WebSocketClient clientSocket = outputs.get(toNodeName);
            if (clientSocket != null) {
                clientSocket.send(message);
            }else{
                URI uri = URI.create("ws://localhost:8081/wwwww");
                clientSocket = new WebSocketClient(uri) {
                    @Override
                    public void onOpen(ServerHandshake handshakedata) {

                    }

                    @Override
                    public void onMessage(String message) {
                        WsClientServer.this.onMessage(message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {

                    }

                    @Override
                    public void onError(Exception ex) {

                    }
                };
                outputs.put(toNodeName, clientSocket);
            }
        }
    }

    private void startServer(int port) {
        server = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                inputs.put(handshake.getResourceDescriptor().substring(1), conn);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {

            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                WsClientServer.this.onMessage(message);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {

            }

            @Override
            public void onStart() {

            }
        };
    }

    void onMessage(String messageStr) {
         Message message = json.fromJson(messageStr, Message.class);
         onMessage(message);
    }

    void onMessage(Message message) {
        if (message.destination == null || message.destination.equals(nodeName)) {// node is finded

        } else {
            int traceIndex = message.trace.indexOf(nodeName);
            if (traceIndex != -1) {// retranslate
                WebSocket nextNode = inputs.get(traceIndex - 1);
                nextNode.send(json.toJson(message));
            } else { // find node
                int minDist = message.destination.length();
                WebSocket minConn = null;
                for (String name : inputs.keySet()) {
                    if (message.trace.indexOf(name) == -1) {
                        int dist = StringDistance.calculate(name, message.destination);
                        if (dist < minDist) {
                            minDist = dist;
                            minConn = inputs.get(name);
                        }
                    }
                }
                message.trace.add(nodeName);
                if (minConn != null)
                    minConn.send(json.toJson(message));
            }
        }
    }
}
