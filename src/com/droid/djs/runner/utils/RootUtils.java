package com.droid.djs.runner.utils;

import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.net.ws.WsClientServer;

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
            WsClientServer.getInstance().sendGui(node);
            return null;
        }, par("observer_id", NodeType.STRING),
                par("key", NodeType.NODE),
                par("value", NodeType.NODE));
    }
}
