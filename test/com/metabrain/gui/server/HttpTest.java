package com.metabrain.gui.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpTest {

    @Test
    void serve() {
        try {
            Server server = new Server(9080);
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}