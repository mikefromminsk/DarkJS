package com.droid.djs.treads;

import com.droid.djs.serialization.js.JsParser;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.ThreadNode;
import com.droid.djs.serialization.js.JsBuilder;
import com.droid.instance.Instance;

import java.util.List;

public class Threads {

    private ThreadNode findThread(Node node) {
        NodeBuilder builder = new NodeBuilder();
        Node item = node;
        while (item.type != NodeType.THREAD)
            item = builder.set(item).getLocalParentNode();
        return (ThreadNode) item;
    }

    public boolean run(Node node, Node[] args, boolean async, Long accessToken) {
        return findThread(node).run(node, args, async, accessToken);
    }

    public boolean run(Node node, Node[] args, boolean async) {
        return findThread(node).run(node, args, async, Instance.get().accessToken);
    }

    public boolean run(Node node, Node[] args) {
        return findThread(node).run(node, args, false, Instance.get().accessToken);
    }

    public boolean run(Node node) {
        return findThread(node).run(node, null, false, Instance.get().accessToken);
    }

    private JsBuilder jsBuilder = new JsBuilder();

    public Node runScript(String path, String sourceCode, Long code) {
        Node node = Files.getNode(path);
        Node module = jsBuilder.build(node, JsParser.parse(sourceCode));
        run(module, null, false, code);
        return node;
    }

}
