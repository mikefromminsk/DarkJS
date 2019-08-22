package com.droid.instance;

import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.serialization.node.HttpResponse;
import com.droid.djs.serialization.node.NodeSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
    @Test
    void test() throws IOException {
        Instance server = new Instance("out/storeServer", true)
                .setNodeName("store.node")
                .setAccessCode("john", "1234")
                .load("server.node.js", "var serverData = 12")
                .call("server")
                .call(() -> {
                    String data = NodeSerializer.toJson(Files.getNode("server/serverData"));
                    assertNotEquals(-1, data.indexOf("12.0"));
                });

        Instance cleint = new Instance("out/storeClient", true)
                .setProxyHost("localhost", server.portAdding)
                .setAccessCode("john", "1234")
                .load("client.node.js",
                        "function getCode(){\n" +
                                "    return get(\"store.node/server/serverData\")\n" +
                                "}")
                .call("client/getCode")
                .call(() -> {
                    String data = NodeSerializer.toJson(Files.getNode("client/getCode"));
                    assertNotEquals(-1, data.indexOf("12.0"));
                });
    }
}