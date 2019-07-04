package com.droid.djs.runner.utils;

import com.droid.djs.consts.NodeType;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.serialization.node.NodeSerializer;

public class NodeUtils extends Utils {
    @Override
    public String name() {
        return "Node";
    }

    @Override
    public void methods() {
        func("body", (builder, node, ths) -> {
            String path = leftString(builder, node);
            Double level = rightNumber(builder, node);
            if (level == null)
                level = 15d;
            Node pathNode = Files.getNode(path);
            String json = NodeSerializer.toJson(pathNode, level.intValue());
            return builder.create(NodeType.STRING).setData(json).commit();
        }, par("path", NodeType.STRING), par("level", NodeType.NUMBER));
    }
}
