package com.droid.djs.runner.utils;

import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.instance.Instance;
import com.droid.net.ws.WsClientServer;

import java.util.Arrays;

public class Net extends Utils {
    @Override
    public String name() {
        return "Net";
    }

    @Override
    public void methods() {

        // TODO dynamic params
        func("send", (builder, node, ths) -> {
                    System.out.println("Net.send");
                    String to = firstString(builder, node);
                    String path = secondString(builder, node);
                    Node[] messageParams = builder.set(node).getParams();
                    Node[] receiverParams = Arrays.copyOfRange(messageParams, 2, messageParams.length);
                    Instance.get().getWsClientServer().send(to, path, receiverParams);
                    return builder.createBool(true);
                }, par("to", NodeType.STRING),
                par("receiver", NodeType.STRING),
                par("first", NodeType.STRING),
                par("second", NodeType.STRING));

    }
}
