package com.droid.djs.runner.utils;

import com.droid.djs.nodes.consts.NodeType;
import com.droid.instance.Instance;

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

        // TODO add dynamic count of params
        func("gui", (builder, node, ths) -> {
            Instance.get().startWsClientServer().sendGui(node);
            return null;
        }, par("observer_id", NodeType.STRING),
                par("key", NodeType.NODE),
                par("value", NodeType.NODE));
    }
}
