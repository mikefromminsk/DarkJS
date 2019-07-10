package com.droid.djs.runner.utils;

import com.droid.djs.nodes.consts.NodeType;

public class RootUtils extends Utils {
    @Override
    public String name() {
        return "/";
    }

    @Override
    public void methods() {
        func("data", (builder, node, ths) -> {
            return null;
        }, par("hash", NodeType.STRING));
    }
}
