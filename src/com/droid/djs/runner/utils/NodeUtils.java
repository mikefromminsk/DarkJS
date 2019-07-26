package com.droid.djs.runner.utils;

import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.fs.Files;

public class NodeUtils extends Utils {
    @Override
    public String name() {
        return "Node";
    }

    @Override
    public void methods() {
        func("get", (builder, node, ths) -> {
            String path = firstString(builder, node);
            return Files.getNodeIfExist(path);
        }, par("path", NodeType.STRING));

        func("path", (builder, node, ths) -> {
            String path = Files.getPath(node);
            return builder.createString(path);
        });
    }
}
