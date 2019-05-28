package com.metabrain.gui;

import com.metabrain.net.http.Main;
import org.junit.jupiter.api.Test;

import java.net.URI;

class MainTest {

    @Test
    void openWebpage() {
        Main.openWebpage(URI.create("http://localhost:8090"));
    }
}