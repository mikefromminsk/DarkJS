package com.droid;

import com.droid.instance.Instance;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String login = "john";
        String pass = "1234";

        Instance proxy = new Instance("out/MainTest/proxy", true)
                .setAccessCode(login, pass)
                .start();

        Instance store = new Instance("out/MainTest/store", true)
                .setProxyPortAdding(proxy.portAdding)
                .setNodeName("store.node")
                .setAccessCode(login, pass)
                .load("C:/wamp/www/droid")
                .start();

        Instance localnode = new Instance("out/MainTest/client", true)
                .setProxyPortAdding(proxy.portAdding)
                .setAccessCode(login, pass)
                .load("C:/wamp/www/droid")
                .loadExcept("/summator")
                .start();

        localnode.getHttpClientServer().join();
    }
}
