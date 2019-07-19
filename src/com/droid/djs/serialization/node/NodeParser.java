package com.droid.djs.serialization.node;

import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.consts.LinkType;
import com.droid.djs.nodes.consts.NodeType;

import java.util.*;

public class NodeParser {

    private static void setLink(NodeBuilder builder, Node node, LinkType linkType, String linkStr,
                                Map<String, Map<String, Object>> nodes, Map<String, Node> replacementTable) {
        if (linkType == LinkType.NATIVE_FUNCTION_NUMBER) {
            builder.set(node).setFunctionIndex(Integer.valueOf(linkStr)).commit();
        } else {
            Node linkValueNode = null;

            if (linkStr.equals(NodeSerializer.TRUE) || linkStr.equals(NodeSerializer.FALSE))
                linkValueNode = builder.create(NodeType.BOOL)
                        .setData(linkStr)
                        .commit();
            if (linkStr.charAt(0) >= '0' && linkStr.charAt(0) <= '9' || linkStr.charAt(0) == '-')
                linkValueNode = builder.create(NodeType.NUMBER)
                        .setData(linkStr)
                        .commit();
            if (linkStr.startsWith(NodeSerializer.STRING_PREFIX))
                linkValueNode = builder.create(NodeType.STRING)
                        .setData(linkStr.substring(NodeSerializer.STRING_PREFIX.length()))
                        .commit();

            if (linkStr.startsWith(NodeSerializer.NODE_PREFIX))
                linkValueNode = parseNode(builder, linkStr, nodes, replacementTable);

            builder.set(node).setLink(linkType, linkValueNode);
        }
    }

    public static Node parseNode(NodeBuilder builder, String nodeName,
                                 Map<String, Map<String, Object>> nodes, Map<String, Node> replacementTable) {
        Node node = replacementTable.get(nodeName);

        Map<String, Object> links = nodes.get(nodeName);

        if (node != null) {
            String nodeTypeStr = (String) links.get(NodeSerializer.TYPE_KEY);
            NodeType nodeType = nodeTypeStr != null ? NodeType.valueOf(nodeTypeStr) : NodeType.NODE;
            node = builder.create(nodeType).commit();
            replacementTable.put(nodeName, node);
        }

        for (String linkName : links.keySet()) {
            LinkType linkType = LinkType.valueOf(linkName);
            Object obj = links.get(linkName);
            if (obj instanceof ArrayList) {
                for (Object item : (ArrayList) obj)
                    if (item instanceof String)
                        setLink(builder, node, linkType, (String) item, nodes, replacementTable);
            } else {
                setLink(builder, node, linkType, (String) obj, nodes, replacementTable);
            }
        }
        return builder.set(node).commit();
    }

    public static Node parse(Map<String, Map<String, Object>> nodes) {
        return parseNode(new NodeBuilder(), nodes.entrySet().iterator().next().getKey(), nodes, new HashMap<>());
    }
}
