package org.pdk.modules;

import org.pdk.storage.NodeBuilder;
import org.pdk.storage.model.node.Node;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

abstract public class Module {

    public NodeBuilder builder;
    public Node node;
    public Map<ByteBuffer, Node> functions = new HashMap<>();

    public Module(NodeBuilder builder) {
        this.builder = builder;
        this.node = builder.getNodeFromRoot(path());
        methods();
    }

    public abstract String path();

    public abstract void methods();

    public void func(String name, Func func, String... args) {
        Node funcNode = builder.getNode(node, name.getBytes());
        funcNode.func = func;
        for (String paramName: args) {
            Node param = builder.create().setTitle(paramName).commit();
            builder.set(funcNode).addParam(param).commit();
            functions.put(ByteBuffer.wrap(paramName.getBytes()), param);
        }
    }
}
