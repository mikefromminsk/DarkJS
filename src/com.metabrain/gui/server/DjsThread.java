package com.metabrain.gui.server;

import com.metabrain.djs.refactored.Formatter;
import com.metabrain.djs.refactored.Runner;
import com.metabrain.djs.refactored.node.Node;
import com.metabrain.djs.refactored.node.NodeBuilder;

import java.util.Map;

public class DjsThread extends Runner {

    private NodeBuilder builder = new NodeBuilder();

    String getNode(Long nodeId) {
        Node node = builder.get(nodeId).getNode();
        return Formatter.toJson(node);
    }

    void updateNode(String body) {
        Map<String, Map<String, Object>> bodyMap = Formatter.fromJson(body);
        for (String key : bodyMap.keySet()) {

        }
    }

    String setNode(Long nodeId, String body) {
        updateNode(body);
        return getNode(nodeId);
    }

    String runNode(Long nodeId, String body) {
        updateNode(body);
        Node node = builder.get(nodeId).getNode();
        run(node);
        return getNode(nodeId);
    }
}
