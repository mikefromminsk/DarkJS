package org.pdk.modules.root;

import org.pdk.modules.Module;
import org.pdk.store.model.node.Node;
import org.pdk.store.consts.NodeType;
import org.pdk.files.convertors.node.NodeSerializer;
import org.pdk.instance.Instance;

import java.io.IOException;

public class RootModule extends Module {
    @Override
    public String name() {
        return "/";
    }

    @Override
    public void methods() {
        func("get",
                (builder, ths) -> {
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

        func("data", (builder, ths) -> {
            return null;
        }, par("hash", NodeType.STRING));

        // TODO add dynamic count of params
        func("gui", (builder, ths) -> {
                    Instance.get().startWsClientServer().broadcast(NodeSerializer.toJson(builder.getNode()));
                    return null;
                }, par("observer_id", NodeType.STRING),
                par("data", NodeType.NODE));
    }
}
