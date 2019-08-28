package com.droid.net.ws;

import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.serialization.node.NodeParser;
import com.droid.djs.serialization.node.NodeSerializer;
import com.droid.instance.Instance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;

public class WsClientServer extends WebSocketServer {

    private String proxyHost;

    private final String nodeName;
    public static int defaultPort = 59000;

    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    private static List<WebSocket> gui = new ArrayList<>();
    public static Map<String, WebSocket> incoming = new HashMap<>();
    private WebSocketClient proxy;

    private static List<Message> messageBuffer = new ArrayList<>();

    public WsClientServer(Integer port) {
        super(new InetSocketAddress(port == null ? defaultPort : port));
        this.nodeName = "node" + port;
        start();
        proxy = new WsProxyClient();
        proxy.connect();
    }

    @Override
    public void onMessage(WebSocket conn, String messageStr) {
        Message message = json.fromJson(messageStr, Message.class);
        onTransmitMessage(message);
    }

    private boolean isThisNode(String nodename) {
        return nodename == null || nodename.equals(getNodeName());
    }

    public void sendGui(Node nodeWithParams) {
        for (WebSocket client : gui)
            client.send(NodeSerializer.toJson(nodeWithParams));
    }

    public void send(String nodename, String receiverPath, Node[] args) {
        if (isThisNode(nodename)) {
            onDestinationMessage(receiverPath, args, Instance.get().accessToken);
        } else {
            onTransmitMessage(new Message(nodename, receiverPath, NodeSerializer.toList(args), Instance.get().accessToken));
        }
    }

    private void onTransmitMessage(Message message) {
        System.out.println("transmit " + getAddress().toString());
        if (isThisNode(message.destNodeName)) {
            onDestinationMessage(message.receiverPath, NodeParser.fromList(message.args), message.accessCode);
        } else {
            WebSocket serverSocket = incoming.get(message.destNodeName);
            if (serverSocket != null) {
                serverSocket.send(json.toJson(message));
            } else {
                if (proxy != null && proxy.isOpen())
                    proxy.send(json.toJson(message));
                else {
                    messageBuffer.add(message);
                    proxy = new WsProxyClient();
                    proxy.connect();
                }
            }
        }
    }

    void onDestinationMessage(String receiverPath, Node[] args, Long accessCode) {
        System.out.println("out " + receiverPath);
        Node receiver = Files.getNodeIfExist(receiverPath);
        if (receiver != null)
            Instance.get().getThreads().run(receiver, args, true, accessCode);
    }

    public String getNodeName() {
        return nodeName;
    }

    public class WsProxyClient extends WebSocketClient {

        public WsProxyClient() {
            super(URI.create("ws://" + proxyHost + "/" + getNodeName()));
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            proxy = this;
            for (Message message : messageBuffer)
                proxy.send(json.toJson(message));
            messageBuffer.clear();
        }

        @Override
        public void onMessage(String message) {
            onTransmitMessage(json.fromJson(message, Message.class));
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            proxy = null;
        }

        @Override
        public void onError(Exception ex) {

        }
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
        incoming.values().remove(conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
    }

}
