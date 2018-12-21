package com.metabrain.gui;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void openWebpage() {
        Main.openWebpage(URI.create("http://localhost:8090"));
    }
}