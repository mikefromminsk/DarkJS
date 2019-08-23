package com.droid.djs.runner;

import com.droid.Main;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.Node;
import com.droid.djs.serialization.node.NodeSerializer;
import com.droid.instance.Instance;
import com.droid.djs.treads.Threads;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RunnerTest {

    void notNull(Object obj, Node test){
        if (obj == null)
            System.out.println(NodeSerializer.toJson(test));
        assertNotNull(obj);
    }

    void isTrue(Boolean bool, Node test){
        if (bool == null || !bool)
            System.out.println(NodeSerializer.toJson(test));
        assertNotNull(bool);
        assertTrue(bool);
    }

    //TODO @RepeatedTest(100)
    @Test
    void run() throws IOException {
        new Instance("out/runTests", true)
                .load("test_res/run")
                .call(() -> {
                    NodeBuilder builder = new NodeBuilder().set(Instance.get().getMaster());
                    for (Node test : builder.getLocalNodes()) {
                        Instance.get().getThreads().run(test);
                        Node testVar = builder.set(test).findLocal("test");
                        notNull(testVar, test);
                        Node testValue = builder.set(testVar).getValueNode();
                        notNull(testValue, test);
                        Boolean testData = (Boolean) builder.set(testValue).getData().getObject();
                        notNull(testData, test);
                        isTrue(testData, test);
                    }
                }).stop();
    }
}