package com.droid.net.ws;

import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.serialization.node.NodeParser;
import com.droid.djs.serialization.node.NodeSerializer;
import com.droid.djs.treads.Secure;
import com.droid.djs.treads.Threads;
import com.droid.gdb.DiskManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class WsClientServer extends WebSocketServer {


    public static int defaultPort = 8081;
    public int port;
    public String nodeName;
    private static WsClientServer instance;

    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    private static List<WebSocket> gui = new ArrayList<>();
    private static Map<String, WebSocket> incoming = new HashMap<>();
    private static Map<String, WebSocketClient> outgoing = new HashMap<>();

    private static Map<String, List<Message>> messages = new HashMap<>();


    public WsClientServer(Integer port, String nodeName) {
        instance = this;
        this.port = port == null ? defaultPort : port;
        this.nodeName = nodeName == null ? "" + DiskManager.getInstance().device_id : nodeName;
    }

    public WsClientServer() {
        this(null, null);
    }

    public WsClientServer(int port) {
        this(port, null);
    }

    public WsClientServer(String nodeName) {
        this(null, nodeName);
    }

    public static WsClientServer getInstance() {
        if (instance == null) {
            instance = new WsClientServer();
            instance.start();
        }
        return instance;
    }

    public void sendGui(Node nodeWithParams) {
        for (WebSocket client : gui)
            client.send(NodeSerializer.toJson(nodeWithParams));
    }

    public void send(String to, String receiverPath, Node node) {
        if (to == null)
            return;
        if (to.equals(nodeName) || to.equals("localhost")) {
            onDestinationMessage(receiverPath, node, Secure.selfAccessCode);
        } else {
            send(to, receiverPath, NodeSerializer.toMap(node));
        }
    }

    public void send(String to, String receiverPath, Map<String, Map<String, Object>> map) {
        if (to == null)
            return;
        if (to.equals(nodeName) || to.equals("localhost")) {
            onDestinationMessage(receiverPath, NodeParser.parse(map), Secure.selfAccessCode);
        } else {
            Message message = new Message(to, receiverPath, map, Secure.selfAccessCode);
            WebSocket serverSocket = incoming.get(to);
            if (serverSocket != null) {
                serverSocket.send(json.toJson(message));
                return;
            }
            WebSocketClient clientSocket = outgoing.get(to);
            if (clientSocket != null) {
                clientSocket.send(json.toJson(message));
                return;
            } else {
                clientSocket = createConnect(to);
                clientSocket.connect();
                List<Message> list = messages.get(to);
                if (list == null)
                    messages.put(to, list = new ArrayList<>());
                list.add(message);
            }
        }
    }

    private WebSocketClient createConnect(String to) {
        return new WebSocketClient(URI.create(to + "/" + nodeName)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                outgoing.put(to, this);
                List<Message> list = messages.get(to);
                for (Message message : list)
                    this.send(json.toJson(message));
                messages.remove(to);
            }

            @Override
            public void onMessage(String message) {
                onTransmitMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                outgoing.values().remove(this);
            }

            @Override
            public void onError(Exception ex) {
                connectError(to, this);
            }
        };
    }

    private void connectError(String to, WebSocketClient client) {

        int serverMinDifference = to.length();
        WebSocket serverConnection = null;
        for (String destination : incoming.keySet()) {
            int destinationDistance = StringDistance.calculate(to, destination);
            if (destinationDistance <= serverMinDifference) {
                serverMinDifference = destinationDistance;
                serverConnection = incoming.get(destination);
            }
        }
        int clientMinDifference = to.length();
        WebSocketClient clienctConnection = null;
        for (String destination : outgoing.keySet()) {
            int destinationDistance = StringDistance.calculate(to, destination);
            if (destinationDistance <= clientMinDifference) {
                clientMinDifference = destinationDistance;
                clienctConnection = outgoing.get(destination);
            }
        }

        List<Message> list = messages.get(to);
        for (Message message : list) {
            if (message.trace == null)
                message.trace = new ArrayList<>();
            message.trace.add(nodeName);
            String messageStr = json.toJson(message);
            if (serverConnection != null && serverMinDifference >= clientMinDifference) {
                serverConnection.send(messageStr);
            } else if (clienctConnection != null) {
                clienctConnection.send(messageStr);
            } else {
                receiveError(to);
            }
        }
    }

    private void receiveError(String to) {
        System.out.println("receiveError");
        messages.remove(to);
    }


    @Override
    public void stop() {
        try {
            super.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (WebSocket guiClietn : gui)
            guiClietn.close();
        for (WebSocketClient client : outgoing.values())
            client.close();
        instance = null;
    }

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
        onTransmitMessage(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

    }

    private void onTransmitMessage(String messageStr) {
        Message message = json.fromJson(messageStr, Message.class);
        if (message.destination == null || message.destination.equals(nodeName)) { // node is finded
            onDestinationMessage(message.receiverPath, NodeParser.parse(message.node), message.accessCode);
        } else {
            // send(message.destination, message.receiverPath, message.node)
            int traceIndex = message.trace == null ? -1 : message.trace.indexOf(nodeName);
            if (traceIndex != -1) {
                // loop
            } else {
                // find

            }
        }
    }

    void onDestinationMessage(String receiverPath, Node node, Long selfAccessCode) {
        Node receiver = Files.getNodeIfExist(receiverPath);
        if (receiver != null) {
            Node[] messageParams = new NodeBuilder().set(node).getParams();
            Node[] receiverParams = Arrays.copyOfRange(messageParams, 2, messageParams.length);
            Threads.getInstance().run(receiver, receiverParams, true, selfAccessCode);
        }
    }

}
