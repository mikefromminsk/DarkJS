package com.metabrain.gui;

import com.metabrain.net.http.HttpTest;
import org.junit.jupiter.api.Test;

import java.net.URI;

class HttpTestTest {

    @Test
    void openWebpage() {
        HttpTest.openWebpage(URI.create("http://localhost:8090"));
    }
}