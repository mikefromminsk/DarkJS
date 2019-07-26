package com.droid.net.ws;

import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.serialization.node.NodeParser;
import com.droid.djs.serialization.node.NodeSerializer;
import com.droid.djs.treads.Secure;
import com.droid.djs.treads.Threads;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;

public class WsClientServer {

    private WebSocketServer server;

    private int port;
    public static String nodeName;
    public static String ip;

    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    private static List<WebSocket> gui = new ArrayList<>();
    private static Map<String, WebSocket> incoming = new HashMap<>();
    private static Map<String, WebSocketClient> outgoing = new HashMap<>();

    public static WsClientServer instance;

    public WsClientServer(int port, String nodeName) {
        this.port = port;
        this.ip = "172.17.0.70";
        WsClientServer.nodeName = nodeName;
        instance = this;
    }

    public void sendGui(Node nodeWithParams) {
        for (WebSocket client : gui)
            client.send(NodeSerializer.toJson(nodeWithParams));
    }

    public void send(String to, String path, Node node) {
        if (to == null)
            return;
        if (to.equals(nodeName) || to.equals(ip)) {
            onMessage(path, node, Secure.selfAccessCode);
        } else {
            WebSocket serverSocket = incoming.get(to);
            Message message = new Message(to, path, NodeSerializer.toMap(node));
            if (serverSocket != null)
                serverSocket.send(json.toJson(message));
        }
    }

    void onMessage(String receiverPath, Node node, Long selfAccessCode) {
        Node receiver = Files.getNode(receiverPath);
        Node[] messageParams = new NodeBuilder().set(node).getParams();
        Node[] receiverParams = Arrays.copyOfRange(messageParams, 2, messageParams.length);
        Threads.getInstance().run(receiver, receiverParams, true, selfAccessCode);
    }

    void transmit(Message message) {
        if (message.destination == null || message.destination.equals(nodeName)) { // node is finded
            Node node = Files.getNodeIfExist(message.path);
            if (node != null) {
                Node receivedNode = NodeParser.parse(message.nodes);
                Files.putNode(node, message.path, receivedNode);
            }
        } else {
            /*int traceIndex = message.trace.indexOf(nodeName);
            if (traceIndex != -1) {// retranslate
                WebSocket nextNode = incoming.get(traceIndex - 1);
                nextNode.send(json.toJson(message));
            } else { // find node
                int minDist = message.destination.length();
                WebSocket minConn = null;
                for (String name : incoming.keySet()) {
                    if (message.trace.indexOf(name) == -1) {
                        int dist = StringDistance.calculate(name, message.destination);
                        if (dist < minDist) {
                            minDist = dist;
                            minConn = incoming.get(name);
                        }
                    }
                }
                message.trace.add(nodeName);
                if (minConn != null)
                    minConn.send(json.toJson(message));
            }*/
        }
    }


    public void start() {
        if (server != null)
            stop();
        server = new WebSocketServer(new InetSocketAddress(port)) {

            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                String name = handshake.getResourceDescriptor().substring(1).toLowerCase();
                System.out.println("connect " + name);
                if (name.equals("gui"))
                    gui.add(conn);
                else
                    incoming.put(name, conn);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                gui.remove(conn);
                outgoing.values().remove(conn);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {

            }

            @Override
            public void onError(WebSocket conn, Exception ex) {

            }

            @Override
            public void onStart() {
                System.out.println("server started");
            }
        };
        server.start();
    }


    public void stop() {
        for (WebSocket guiClietn : gui)
            guiClietn.close();
        for (WebSocketClient client : outgoing.values())
            client.close();
        try {
            server.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        server = null;
    }

    void send(String toNodeName, String message) {
        WebSocket serverSocket = incoming.get(toNodeName);
        if (serverSocket != null) {
            serverSocket.send(message);
        } else {
            WebSocketClient clientSocket = outgoing.get(toNodeName);
            if (clientSocket != null) {
                clientSocket.send(message);
            } else {
                URI uri = URI.create("ws://localhost:8081/wwwww");
                clientSocket = new WebSocketClient(uri) {
                    @Override
                    public void onOpen(ServerHandshake handshakedata) {

                    }

                    @Override
                    public void onMessage(String message) {

                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {

                    }

                    @Override
                    public void onError(Exception ex) {

                    }
                };
                outgoing.put(toNodeName, clientSocket);
            }
        }
    }

}
