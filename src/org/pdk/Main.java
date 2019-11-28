package org.pdk;

import org.pdk.instance.Instance;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
        String login = "john";
        String pass = "1234";

        Instance proxy = new Instance("out/MainTest/proxy", true)
                .setAccessCode(login, pass)
                .start();

        Instance store = new Instance("out/MainTest/store", true)
                .setProxy("localhost", proxy.proxyPortAdding, "store.node")
                .setAccessCode(login, pass)
                .load("gui")
                .loadExcept(".idea")
                .start();

        Instance localnode = new Instance("out/MainTest/clent", true) // clent for clear logs
                .setProxy("localhost", proxy.proxyPortAdding, "client.node")
                .setAccessCode(login, pass)
                .load("gui")
                .loadExcept(".idea")
                .loadExcept("summator")
                .start();

        localnode.getHttpServer().join();
    }
}
