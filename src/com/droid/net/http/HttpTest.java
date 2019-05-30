package com.droid.net.http;

import java.awt.*;
import java.net.URI;

public class HttpTest {

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static int post = 9080;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        try {
            Server server = new Server(post);
            server.start();
            //openWebpage(URI.create("http://localhost:" + post));
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
