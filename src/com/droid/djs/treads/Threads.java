package com.droid.djs.treads;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.consts.NodeType;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.ThreadNode;
import com.droid.djs.serialization.js.JsParser;

import java.util.List;

public class Threads {

    private NodeBuilder builder = new NodeBuilder();
    private static Threads instance;

    public static Threads getInstance() {
        if (instance == null)
            instance = new Threads();
        return instance;
    }

    private ThreadNode findThread(Node node) {
        Node item = node;
        while (item.type != NodeType.THREAD)
            item = builder.set(item).getLocalParentNode();
        return (ThreadNode) item;
    }

    //TODO add args to thread
    public boolean run(Node node, List<Node> args, boolean async, Long access_token) {
        ThreadNode thread = findThread(node);
        return thread.run(node, async, access_token);
    }

    private JsParser jsParser = new JsParser();

    public Node runScript(String path, String sourceCode, Long code) {
        Node node = Files.getNode(path);
        Node module = jsParser.parse(node, sourceCode);
        run(module, null, false, code);
        return node;
    }

}
