package com.droid.djs.treads;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.fs.Files;
import com.droid.djs.consts.NodeType;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.ThreadNode;
import com.droid.djs.serialization.js.Parser;

import java.util.Map;

public class ThreadPool {

    private final static String AUTOLOADING_DIR = "start";
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

    public void addToAutoloading(Node thread) {
        builder.set(Files.getNode(AUTOLOADING_DIR))
                .addNext(thread)
                .commit();
    }

    public void removeFromAutoloading(Node thread) {
        builder.set(Files.getNode(AUTOLOADING_DIR))
                .removeNext(thread)
                .commit();
    }

    public void run(String path) {
        run(Files.getNode(path));
    }

    public void run(String path, Map<String, String> args) {
        run(Files.getNode(path), args, false);
    }

    public void run(Node node) {
        run(node, null, false);
    }

    public void run(Node node, Map<String, String> args, boolean async) {
        findThread(node).run(node, async);
    }

    private Parser parser = new Parser();

    public Node runScript(String path, String sourceCode) {
        Node node = Files.getNode(path);
        Node module = parser.parse(node, sourceCode);
        run(module);
        return node;
    }

    // TODO delete autorun and start all thread children that is thread node
    public void autorun() {
        Node[] autorunList = builder.set(Files.getNode(AUTOLOADING_DIR)).getLocalNodes();
        for (Node autorunItem: autorunList)
            run(autorunItem);
    }
}
