package com.metabrain.gui.server;

import com.metabrain.djs.refactored.Formatter;
import com.metabrain.djs.refactored.Parser;
import com.metabrain.djs.refactored.Runner;
import com.metabrain.djs.refactored.node.LinkType;
import com.metabrain.djs.refactored.node.Node;
import com.metabrain.djs.refactored.node.NodeBuilder;
import com.metabrain.djs.refactored.node.NodeType;
import com.metabrain.gui.server.model.GetNodeBody;
import jdk.nashorn.internal.runtime.ParserException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DjsThread extends Runner {

    private static void setLink(NodeBuilder builder, Node node, byte linkType, Map<String, Node> replacementTable, String itemStr) {
        Node linkValueNode = null;

        if (itemStr.equals(Formatter.TRUE) || itemStr.equals(Formatter.FALSE))
            linkValueNode = builder.create(NodeType.BOOL)
                    .setData(itemStr)
                    .commit();
        if (itemStr.charAt(0) >= '0' && itemStr.charAt(0) <= '9')
            linkValueNode = builder.create(NodeType.NUMBER)
                    .setData(itemStr)
                    .commit();
        if (itemStr.startsWith(Formatter.STRING_PREFIX))
            linkValueNode = builder.create(NodeType.STRING)
                    .setData(itemStr.substring(Formatter.STRING_PREFIX.length()))
                    .commit();
        if (itemStr.startsWith(Formatter.NODE_PREFIX))
            linkValueNode = builder.get(Long.valueOf(itemStr.substring(Formatter.NODE_PREFIX.length())))
                    .getNode();
        if (linkValueNode == null)
            linkValueNode = replacementTable.get(itemStr);
        builder.set(node).setLink(linkType, linkValueNode);
    }

    public void updateNode(GetNodeBody request) {
        NodeBuilder builder = new NodeBuilder();
        Map<String, Node> replacementTable = new HashMap<>();

        for (String nodeStr : request.nodes.keySet()) {
            if (nodeStr.startsWith(Formatter.NEW_NODE_PREFIX)) {
                Node node = builder.create().commit();
                request.replacements.put(nodeStr, Formatter.NODE_PREFIX + node.id);
                replacementTable.put(nodeStr, node);
            }
        }

        for (String nodeStr : request.nodes.keySet()) {
            Node node = replacementTable.get(nodeStr);
            if (node == null && nodeStr.startsWith(Formatter.NODE_PREFIX))
                node = builder.get(Long.valueOf(nodeStr.substring(Formatter.NODE_PREFIX.length()))).getNode();
            Map<String, Object> links = request.nodes.get(nodeStr);

            Object nodeTypeObj = links.get(Formatter.TYPE_PREFIX);
            if (nodeTypeObj instanceof char[]) {
                byte nodeType = NodeType.fromString(new String((char[]) nodeTypeObj));
                if (nodeType == NodeType.NATIVE_FUNCTION) {
                    Object functionIdObj = links.get(Formatter.FUNCTION_ID_PREFIX);
                    builder.create(NodeType.NATIVE_FUNCTION)
                            .setFunctionId(Integer.valueOf(new String((char[]) functionIdObj)))
                            .commit();
                } else if (nodeType != -1) {
                    node.type = nodeType;
                }
            }

            builder.set(node).clearLinks();
            for (String linkName : links.keySet()) {
                Object obj = links.get(linkName);
                byte linkType = LinkType.fromString(linkName);
                if (linkType != -1)
                    if (obj instanceof ArrayList) {
                        for (Object item : (ArrayList) obj)
                            if (item instanceof String)
                                setLink(builder, node, linkType, replacementTable, (String) item);
                    } else if (obj instanceof String) {
                        setLink(builder, node, linkType, replacementTable, (String) obj);
                    }
            }

            builder.set(node).commit();
        }
    }

    void runNode(GetNodeBody getNodeBody) {
    }

    public Node getNode(String nodeLink, Map<String, String> replacements) {
        if (replacements != null && replacements.get(nodeLink) != null)
            nodeLink = replacements.get(nodeLink);
        if (nodeLink.startsWith(Formatter.NODE_PREFIX)) {
            Long nodeId = Long.valueOf(nodeLink.substring(Formatter.NODE_PREFIX.length()));
            return new NodeBuilder().get(nodeId).getNode();
        }
        return null;
    }

    public void parse(Node module, GetNodeBody body) throws ParserException {
        Parser parser = new Parser();
        parser.parse(module, body.source_code);
    }
}
