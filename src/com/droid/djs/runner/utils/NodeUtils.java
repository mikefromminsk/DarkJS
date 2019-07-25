package com.droid.djs.runner.utils;

import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Data;
import com.droid.djs.nodes.Node;
import com.droid.djs.serialization.node.NodeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class NodeUtils extends Utils {
    @Override
    public String name() {
        return "Node";
    }

    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    class NodeBody{
        Long nodeId;
        String nodeName;
        Map<String, Map<String, Object>> nodes;

        public NodeBody(Long nodeId, Map<String, Map<String, Object>> nodes) {
            this.nodeId = nodeId;
            this.nodeName = NodeSerializer.NODE_PREFIX + nodeId;
            this.nodes = nodes;
        }
    }

    @Override
    public void methods() {
        func("get", (builder, node, ths) -> {
            String path = firstString(builder, node);
            return Files.getNodeIfExist(path);
        }, par("path", NodeType.STRING));

        func("path", (builder, node, ths) -> builder.create(NodeType.STRING).setData(Files.getPath(node)).commit());
    }
}
