package com.metabrain.gui.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void join() {
        try {
            Server server = new Server(9080);
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}