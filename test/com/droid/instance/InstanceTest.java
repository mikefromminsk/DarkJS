package com.droid.instance;

import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.serialization.node.HttpResponse;
import com.droid.djs.serialization.node.NodeSerializer;
import org.junit.jupiter.api.Test;

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
    void test() {
        Instance server = new Instance("out/storeServer")
                .setNodeName("store.node")
                .load("/root/storeserver.node.js", "var serverData = 12")
                .startAndWaitInit();

        Instance cleint = new Instance("out/storeClient")
                .setProxyHost("localhost", server.portAdding)
                .load("/root/storeclient.node.js",
                        "function getCode(){\n" +
                                "    return get(\"store.node/root/storeserver/storeData\")\n" +
                                "}")
                .start();

        HttpResponse response = cleint.call("/root/storeclient/getCode", "chat");
        System.out.println(response.data);

        assertNotEquals(-1, new String(response.data).indexOf("12.0"));
        server.stop();
        cleint.stop();
    }
}