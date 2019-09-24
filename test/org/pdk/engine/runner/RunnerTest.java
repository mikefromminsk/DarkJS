package org.pdk.engine.runner;

import org.pdk.store.nodes.NodeBuilder;
import org.pdk.store.nodes.Node;
import org.pdk.convertors.node.NodeSerializer;
import org.pdk.instance.Instance;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
                    //Files.observe("/", node -> System.out.println(Files.getPath(node)));
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