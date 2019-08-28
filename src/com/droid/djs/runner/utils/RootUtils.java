package com.droid.djs.runner.utils;

import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.instance.Instance;

import java.io.IOException;
import java.util.Base64;

public class RootUtils extends Utils {
    @Override
    public String name() {
        return "/";
    }

    @Override
    public void methods() {
        func("get",
                (builder, node, ths) -> {
                    String host = getString(builder, 0);
                    String path = getString(builder, 1);
                    Node parameter = getNode(builder, 2);
                    try {
                        return Instance.get().startHttpServerOnFreePort().requestToProxy(host, path, parameter);
                    } catch (IOException e) {
                        return null;
                    }
                },
                par("host", NodeType.STRING),
                par("path", NodeType.STRING),
                par("arguments", NodeType.NODE));

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
