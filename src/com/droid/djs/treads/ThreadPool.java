package com.droid.djs.treads;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.fs.Files;
import com.droid.djs.consts.NodeType;
import com.droid.djs.nodes.Node;
import com.droid.djs.serialization.js.Parser;

import java.util.Map;

public class ThreadPool {

    private static ThreadPool threadPool;

    public static ThreadPool getInstance() {
        if (threadPool == null)
            threadPool = new ThreadPool();
        return threadPool;
    }

    private NodeBuilder builder = new NodeBuilder();

    final static String AUTOLOADING_DIR = "start";

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
        run(Files.getNode(path), args);
    }

    public void run(Node node) {
        run(node, null);
    }

    public void run(Node node, Map<String, String> args) {

    }

    private Parser parser = new Parser();

    public void runScript(String path, String sourceCode) {
        Node thread = Files.getNode(path, NodeType.THREAD);
        Node module = parser.parse(thread, sourceCode);
        ThreadNode threadNode = findThread(module);
        threadNode.run(module);
    }

    private ThreadNode findThread(Node module) {
        return null;
    }

    // TODO delete autorun and start all thread children that is thread node
    public void autorun() {
        Node[] autorunList = builder.set(Files.getNode(AUTOLOADING_DIR)).getLocalNodes();
        for (Node autorunItem: autorunList)
            run(autorunItem);
    }
}
