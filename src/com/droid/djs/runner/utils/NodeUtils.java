package com.droid.djs.runner.utils;

import com.droid.djs.nodes.Data;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.fs.Files;
import com.droid.djs.serialization.node.NodeParser;
import com.droid.djs.serialization.node.NodeSerializer;

public class NodeUtils extends Utils {
    @Override
    public String name() {
        return "Node";
    }

    @Override
    public void methods() {
        func("get", (builder, ths) -> Files.getNodeIfExist(firstString(builder)), par("path", NodeType.STRING));
        func("serialize", (builder, ths) -> {
            String path = firstString(builder);
            Node serializeNode = Files.getNode(path);
            String json = NodeSerializer.toJson(serializeNode);
            return builder.createString(json);
        }, par("path", NodeType.STRING));
        func("eval", (builder, ths) -> {
                    String nodePath = firstString(builder);
                    String serializeNodeJson = secondString(builder);
                    Node selfNode = Files.getNode(nodePath);
                    Node function = NodeParser.fromJson(serializeNodeJson);
                    Files.replace(selfNode, function);
                    return function;
                }, par("nodePath", NodeType.STRING),
                par("serializeNodeJson", NodeType.STRING));
    }
}
