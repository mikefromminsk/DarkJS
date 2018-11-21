package com.metabrain.gui;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpTest {

    @Test
    void getMac() {

        NanoHTTPD ds = new Http(20000);
        try {
            ds.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}