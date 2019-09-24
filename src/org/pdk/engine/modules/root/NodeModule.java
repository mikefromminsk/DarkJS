package org.pdk.engine.modules.root;

import org.pdk.engine.modules.utils.Module;
import org.pdk.engine.store.nodes.Node;
import org.pdk.engine.store.NodeType;
import org.pdk.engine.files.Files;
import org.pdk.engine.convertors.node.NodeParser;
import org.pdk.engine.convertors.node.NodeSerializer;

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
