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
                .load("/root/storetest.node.js", "var serverData = 12")
                .startAndWaitInit();
        Instance cleint = new Instance("out/storeClient")
                .setProxyHost("localhost", server.portAdding)
                .load("/root/storetest.node,js",
                        "function getCode(appName){\n" +
                                "    var code = get(\"store.node/root/storetest/storeData\" + appName)\n" +
                                "}")
                .start();
        HttpResponse response = cleint.call("/root/storetest/getCode", "chat");

        assertEquals("12", response.data);
        server.stop();
        cleint.stop();
    }
}