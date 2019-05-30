package com.droid.net.http;

import org.junit.jupiter.api.Test;

class HttpServerTest {

    @Test
    void join() {
        try {
            HttpServer httpServer = new HttpServer(HttpServer.debugPort);
            httpServer.start();
            httpServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}