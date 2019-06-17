package com.droid.djs.treads;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.fs.Files;
import com.droid.djs.consts.NodeType;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.ThreadNode;
import com.droid.djs.serialization.js.Parser;

import java.util.List;
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

    public void run(Node node) {
        run(node, null, false);
    }

    public void run(Node node, boolean async) {
        run(node, null, async);
    }

    public void run(Node node, List<Node> args, boolean async) {
        ThreadNode thread = findThread(node);
        thread.run(node, async);
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
