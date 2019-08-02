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

import java.net.URI;
import java.util.*;

public class WsClientServer extends WebSocketServer {

    private String[] proxyServers = new String[]{
            "172.168.0.70:9001"
    };

    public static int defaultPort = 8081;
    public int port;
    public String nodeName;
    private static WsClientServer instance;

    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    private static List<WebSocket> gui = new ArrayList<>();
    public static Map<String, WebSocket> incoming = new HashMap<>();
    public static Map<String, WebSocketClient> outgoing = new HashMap<>();
    private static WebSocketClient proxy;

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

    public class WsClient extends WebSocketClient {

        String to;

        public WsClient(String to) {
            super(URI.create(to + "/" + nodeName));
            this.to = to;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            outgoing.put(to, this);
            retraceMessages(this, to);
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

        }
    }

    private WebSocketClient createConnect(String to) {
        return new WsClient(to) {
            @Override
            public void onError(Exception ex) {
                if (proxy.isOpen()) {
                    retraceMessages(proxy, to);
                } else {
                    proxy = connectToProxy(0, to);
                    proxy.connect();
                }
            }
        };
    }

    private void retraceMessages(WebSocketClient proxy, String to) {
        List<Message> list = messages.get(to);
        for (Message message : list)
            proxy.send(json.toJson(message));
        messages.remove(to);
    }

    private WebSocketClient connectToProxy(Integer index, String from) {
        return new WsClient(proxyServers[index]) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);

            }
        };
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
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
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
