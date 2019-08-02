package com.droid.net.ws;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class WsClientServerTest {

    @Test
    void send() {
        WsClientServer server1 = new WsClientServer(9000);
        server1.start();
        WsClientServer server2 = new WsClientServer(9001);
        server2.start();

        server1.send("172.168.0.70:9002", "root/app/chat/receive", new HashMap<>());

        WsClientServer server3 = new WsClientServer(9002);
        server3.start();

        server1.send("172.168.0.70:9002", "root/app/chat/receive", new HashMap<>());

    }
}