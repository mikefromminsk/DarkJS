package com.droid.djs.treads;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.consts.NodeType;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.ThreadNode;
import com.droid.djs.serialization.js.Parser;

import java.util.List;

public class ThreadPool {

    // TODO add pool
    private NodeBuilder builder = new NodeBuilder();
    private static ThreadPool threadPool;

    public static ThreadPool getInstance() {
        if (threadPool == null)
            threadPool = new ThreadPool();
        return threadPool;
    }

    private ThreadNode findThread(Node node) {
        Node item = node;
        while (item.type != NodeType.THREAD)
            item = builder.set(item).getLocalParentNode();
        return (ThreadNode) item;
    }

    //TODO add args to thread
    public boolean run(Node node, List<Node> args, boolean async, Long code) {
        ThreadNode thread = findThread(node);
        return thread.run(node, async, code);
    }

    private Parser parser = new Parser();

    public Node runScript(String path, String sourceCode, Long code) {
        Node node = Files.getNode(path);
        Node module = parser.parse(node, sourceCode);
        run(module,  null, false, code);
        return node;
    }

    public void stop() {

    }
}
