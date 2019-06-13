package com.droid.djs.treads;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.fs.Files;
import com.droid.djs.consts.NodeType;
import com.droid.djs.nodes.Node;
import com.droid.djs.prototypes.Console;
import org.junit.jupiter.api.Test;

class TreadPoolTest {

    @Test
    void run() {
        NodeBuilder builder = new NodeBuilder();
        NodeBuilder builder2 = new NodeBuilder();

        String threadPath = "app1";

        Node thread = Files.getNode(threadPath);
        thread.type = NodeType.THREAD;
        Node logFunction = builder.create(NodeType.NATIVE_FUNCTION)
                .setFunctionId(Console.LOG)
                .addParam(builder2.create(NodeType.STRING).setData("app1 log message").commit())
                .commit();
        builder.set(thread)
                .addNext(logFunction)
                .commit();
        ThreadPool.getInstance().runScript(threadPath);
        ThreadPool.getInstance().addToAutoloading(thread);
        ThreadPool.getInstance().run(threadPath, null);
        ThreadPool.getInstance().removeFromAutoloading(thread);
    }

}