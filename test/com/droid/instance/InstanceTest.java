package com.droid.instance;

import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstanceTest {

    @Test
    void test(){
        Instance first = new Instance(new InstanceParameters("out/storeClient").setProxyHost("localhost"));
        first.call(() -> {
                System.out.println("onStart");
            });
        first.call(() -> {
                System.out.println("onStart");
                Files.observe("/", node -> System.out.println(Files.getPath(node)));
            });
        System.out.println("onFinish");
    }
}