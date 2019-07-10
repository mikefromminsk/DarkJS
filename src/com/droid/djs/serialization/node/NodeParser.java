package com.droid.djs.serialization.node;

import com.droid.djs.nodes.consts.LinkType;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.nodes.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeParser {
    public static final String FUNCTION_ID_PREFIX = "function_id";

    private static void setLink(NodeBuilder builder, com.droid.djs.nodes.Node node, LinkType linkType, Map<String, Node> replacementTable, String itemStr) {
        com.droid.djs.nodes.Node linkValueNode = null;

        if (itemStr.equals(NodeSerializer.TRUE) || itemStr.equals(NodeSerializer.FALSE))
            linkValueNode = builder.create(NodeType.BOOL)
                    .setData(itemStr)
                    .commit();
        if (itemStr.charAt(0) >= '0' && itemStr.charAt(0) <= '9' || itemStr.charAt(0) == '-')
            linkValueNode = builder.create(NodeType.NUMBER)
                    .setData(itemStr)
                    .commit();
        if (itemStr.startsWith(NodeSerializer.STRING_PREFIX))
            linkValueNode = builder.create(NodeType.STRING)
                    .setData(itemStr.substring(NodeSerializer.STRING_PREFIX.length()))
                    .commit();
        if (itemStr.startsWith(NodeSerializer.NODE_PREFIX))
            linkValueNode = builder.get(Long.valueOf(itemStr.substring(NodeSerializer.NODE_PREFIX.length())))
                    .getNode();
        if (linkValueNode == null)
            linkValueNode = replacementTable.get(itemStr);
        builder.set(node).setLink(linkType, linkValueNode);
    }

    class GetNodeBody {
        public Long threadId;
        public String nodeLink;
        public Boolean run;
        public String source_code;
        public Map<String, String> replacements;
        public Map<String, Map<String, Object>> nodes;
        public String error;
        public List<String> stack;
    }

    public void fromJson(GetNodeBody request) {
        NodeBuilder builder = new NodeBuilder();
        Map<String, com.droid.djs.nodes.Node> replacementTable = new HashMap<>();

        for (String nodeStr : request.nodes.keySet()) {
            if (nodeStr.startsWith(NodeSerializer.NEW_NODE_PREFIX)) {
                com.droid.djs.nodes.Node node = builder.create().commit();
                request.replacements.put(nodeStr, NodeSerializer.NODE_PREFIX + node.id);
                replacementTable.put(nodeStr, node);
            }
        }

        for (String nodeStr : request.nodes.keySet()) {
            com.droid.djs.nodes.Node node = replacementTable.get(nodeStr);
            if (node == null && nodeStr.startsWith(NodeSerializer.NODE_PREFIX))
                node = builder.get(Long.valueOf(nodeStr.substring(NodeSerializer.NODE_PREFIX.length()))).getNode();
            Map<String, Object> links = request.nodes.get(nodeStr);

            Object nodeTypeObj = links.get(NodeSerializer.TYPE_PREFIX);
            if (nodeTypeObj instanceof char[]) {
                NodeType nodeType = NodeType.valueOf(new String((char[]) nodeTypeObj));
                if (nodeType == NodeType.NATIVE_FUNCTION) {
                    Object functionIdObj = links.get(FUNCTION_ID_PREFIX);
                    builder.create(NodeType.NATIVE_FUNCTION)
                            .setFunctionIndex(Integer.valueOf(new String((char[]) functionIdObj)))
                            .commit();
                } /* else if (nodeType != -1) {
                    node.type = nodeType;
                }*/
            }

            builder.set(node).clearLinks();
            for (String linkName : links.keySet()) {
                Object obj = links.get(linkName);
                LinkType linkType = LinkType.valueOf(linkName);
                /* if (linkType != -1)*/
                if (obj instanceof ArrayList) {
                    for (Object item : (ArrayList) obj)
                        if (item instanceof String)
                            setLink(builder, node, linkType, replacementTable, (String) item);
                } else {
                    setLink(builder, node, linkType, replacementTable, "" + obj);
                }
            }

            builder.set(node).commit();
        }
    }
}
