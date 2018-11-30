package com.metabrain.gui.server;

import com.metabrain.djs.refactored.Caller;
import com.metabrain.djs.refactored.Formatter;
import com.metabrain.djs.refactored.Runner;
import com.metabrain.djs.refactored.node.LinkType;
import com.metabrain.djs.refactored.node.Node;
import com.metabrain.djs.refactored.node.NodeBuilder;
import com.metabrain.djs.refactored.node.NodeType;
import com.metabrain.gui.server.model.GetNodeBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DjsThread extends Runner {

    private static void setLink(NodeBuilder builder, Node node, byte linkType, Map<String, Node> replacementTable, char[] itemCharArr) {
        String itemStr = new String(itemCharArr);
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
        if (linkValueNode == null)
            linkValueNode = replacementTable.get(itemStr);
        builder.set(node).setLink(linkType, linkValueNode);
    }

    public static void updateNode(Map<String, Map<String, Object>> bodyMap) {
        NodeBuilder builder = new NodeBuilder();
        Map<String, Node> replacementTable = new HashMap<>();

        for (String nodeStr : bodyMap.keySet()) {
            if (nodeStr.startsWith(Formatter.NODE_PREFIX)) {
                Long nodeId = Long.valueOf(nodeStr.substring(Formatter.NODE_PREFIX.length()));
                replacementTable.put(nodeStr, builder.get(nodeId).getNode());
            }
            if (nodeStr.startsWith(Formatter.NEW_NODE_PREFIX))
                replacementTable.put(nodeStr, builder.create().getNode());
        }

        for (String nodeStr : bodyMap.keySet()) {
            Node node = replacementTable.get(nodeStr);
            Map<String, Object> links = bodyMap.get(nodeStr);

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

            for (String linkName : links.keySet()) {
                Object obj = links.get(linkName);
                byte linkType = LinkType.fromString(linkName);
                if (linkType != -1)
                    if (obj instanceof ArrayList) {
                        for (Object item : (ArrayList) obj)
                            setLink(builder, node, linkType, replacementTable, (char[]) item);
                    } else if (obj instanceof char[]) {
                        setLink(builder, node, linkType, replacementTable, (char[]) obj);
                    }
            }
            
            builder.set(node).commit();
        }

    }


    String getNode(GetNodeBody getNodeBody) {
        /*Node node = builder.get(nodeId).getNode();
        return Formatter.toJson(node);*/
        return null;
    }

    String setNode(GetNodeBody getNodeBody) {
        //updateNode(getNodeBody);
        return null;
    }

    String runNode(GetNodeBody getNodeBody) {
        /*updateNode(body);
        Node node = builder.get(nodeId).getNode();
        run(node);*/
        //return getNode(nodeId);
        return null;
    }
}
