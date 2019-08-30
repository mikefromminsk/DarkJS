package com.droid.net.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WsClientServer extends WebSocketServer {

    public static int defaultPort = 9500;

    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    public WsClientServer(Integer port) {
        super(new InetSocketAddress(port == null ? defaultPort : port));
        start();
    }

    @Override
    public void onMessage(WebSocket conn, String messageStr) {
        Message message = json.fromJson(messageStr, Message.class);

    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("start ws on " + getPort() + " port");
    }

}
