package org.pdk.modules.root;

import org.pdk.modules.Module;
import org.pdk.store.model.node.Node;
import org.pdk.store.consts.NodeType;
import org.pdk.files.Files;
import org.pdk.files.convertors.node.NodeParser;
import org.pdk.files.convertors.node.NodeSerializer;

public class NodeModule extends Module {
    @Override
    public String name() {
        return "Node";
    }

    @Override
    public void methods() {
        func("get", (builder, ths) -> Files.getNodeIfExist(getString(builder, 0)), par("path", NodeType.STRING));
        func("serialize", (builder, ths) -> {
            String path = getString(builder, 0);
            Node serializeNode = Files.getNode(path);
            String json = NodeSerializer.toJson(serializeNode);
            return builder.createString(json);
        }, par("path", NodeType.STRING));
        func("eval", (builder, ths) -> {
                    String nodePath = getString(builder, 0);
                    String serializeNodeJson = getString(builder, 1);
                    Node selfNode = Files.getNode(nodePath);
                    Node function = NodeParser.fromJson(serializeNodeJson);
                    Files.replace(selfNode, function);
                    return function;
                }, par("nodePath", NodeType.STRING),
                par("serializeNodeJson", NodeType.STRING));
    }
}
