package com.droid.instance;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class InstanceTest {

    /*@Test
    void loadingTest() {
        new Instance("out/loadingTest")
                .setNodeName("store.node")
                .load("/root/storetest.node.js", "var serverData = 12")
                .call(() -> {
                    Node node = Files.getNode("/root/storetest/serverData");
                    String jsonData = NodeSerializer.toJson(node);
                    assertNotEquals(-1, jsonData.indexOf("12.0"));
                })
                .stop();
    }*/
/*    @Test
    void test() throws IOException {
        Instance server = new Instance("out/storeServer", true)
                .setNodeName("store.node")
                .setAccessCode("john", "1234")
                .load("server.node.js", "var serverData = 12");
        server.run("server");
        HttpResponse serverResponse = server.get("server/serverData");
        assertEquals("12", new String(serverResponse.data));

        Instance cleint = new Instance("out/storeClient", true)
                .setProxyHost("localhost", server.portAdding)
                .setAccessCode("john", "1234")
                .load("client.node.js",
                        "function getCode(){\n" +
                                "    return get(\"store.node/server/serverData\")\n" +
                                "}");

        HttpResponse getCodeResponse = cleint.get("client/getCode");
        assertEquals("12", new String(getCodeResponse.data));
    }*/

    @Test
    void test() throws IOException {
        /*Instance server = new Instance("out/InstanceTest/server", true)
                .setNodeName("store.node")
                .setAccessCode("john", "1234")
                .load("store.node.js",
                        "function getCode(path) { \n" +
                                "   return Node.serialize(path); \n" +
                                "}")
                .load("server.node.js",
                        "function sum(a, b) { \n" +
                                "   return a + b; \n" +
                                "}")
                .start();
        assertEquals(new Double(3.0d), server.getNumber("server/sum", 1, 2));

        Instance cleint = new Instance("out/InstanceTest/client", true)
                .setProxyPortAdding(server.portAdding)
                .setAccessCode("john", "1234")
                .load("client.node.js",
                        "var storeName = \"store.node\"; \n" +
                        "function loadApp(name){\n" +
                                "    Node.eval(\"server\", get(storeName, \"store/getCode\", name))\n" +
                                "}");
        cleint.run("client/loadApp", "server");
        assertEquals(new Double(4.0d), cleint.getNumber("server/sum", 2, 2));*/
    }

}