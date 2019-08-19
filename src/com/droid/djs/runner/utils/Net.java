package com.droid.djs.runner.utils;

import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.instance.Instance;

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
                    String to = firstString(builder);
                    String path = secondString(builder);
                    Node[] messageParams = builder.set(node).getParams();
                    Node[] receiverParams = Arrays.copyOfRange(messageParams, 2, messageParams.length);
                    Instance.get().startWsClientServer().send(to, path, receiverParams);
                    return builder.createBool(true);
                }, par("to", NodeType.STRING),
                par("receiver", NodeType.STRING),
                par("first", NodeType.STRING),
                par("second", NodeType.STRING));

    }
}
