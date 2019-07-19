package com.droid.djs.runner.utils;

import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.net.ws.WsClientServer;

public class Net extends Utils {
    @Override
    public String name() {
        return "Net";
    }

    @Override
    public void methods() {
        func("send", (builder, node, ths) -> {
            String to = firstString(builder, node);
            String path = secondString(builder, node);
            Node message = builder.set(node).getParamNode(2);
            WsClientServer.send(to, path, message);
            return null;
        }, par("to", NodeType.STRING), par("path", NodeType.STRING), par("message", NodeType.STRING));

        func("name", (builder, node, ths) -> builder.createString(WsClientServer.nodeName));
    }
}
