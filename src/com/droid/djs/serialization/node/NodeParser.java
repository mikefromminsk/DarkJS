package com.droid.djs.serialization.node;

import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.consts.LinkType;
import com.droid.djs.nodes.consts.NodeType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

public class NodeParser {

    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    private static Double parseDouble(String str) {
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    private static void setLink(NodeBuilder builder, Node node, LinkType linkType, String linkStr,
                                Map<String, Map<String, Object>> nodes, Map<String, Node> replacementTable) {
        if (linkType == LinkType.NATIVE_FUNCTION) {
            builder.set(node).setFunctionIndex(Integer.valueOf(linkStr)).commit();
        } else {
            Node linkValueNode = null;

            if (linkStr.equals(NodeSerializer.TRUE))
                linkValueNode = builder.createBool(true);

            if (linkStr.equals(NodeSerializer.FALSE))
                linkValueNode = builder.createBool(false);

            if (linkStr.charAt(0) >= '0' && linkStr.charAt(0) <= '9' || linkStr.charAt(0) == '-')
                linkValueNode = builder.createNumber(parseDouble(linkStr));

            if (linkStr.startsWith(NodeSerializer.STRING_PREFIX))
                linkValueNode = builder.createString(linkStr.substring(NodeSerializer.STRING_PREFIX.length()));

            if (linkStr.startsWith(NodeSerializer.NODE_PREFIX))
                linkValueNode = parseNodeRecursive(builder, linkStr, nodes, replacementTable);

            builder.set(node).setLink(linkType, linkValueNode).commit();
        }
    }

    private static Node parseNodeRecursive(NodeBuilder builder, String nodeName,
                                           Map<String, Map<String, Object>> nodes, Map<String, Node> replacementTable) {
        Node node = replacementTable.get(nodeName);

        if (node == null) {
            Map<String, Object> links = nodes.get(nodeName);

            String nodeTypeStr = (String) links.get(NodeSerializer.TYPE_KEY);
            String nodeDataStr = (String) links.get(NodeSerializer.DATA_KEY);

            NodeType nodeType = nodeTypeStr != null ? NodeType.valueOf(nodeTypeStr.toUpperCase()) : NodeType.NODE;

            if (nodeType == NodeType.BOOLEAN) {
                if (nodeDataStr.equals(NodeSerializer.TRUE))
                    node = builder.createBool(true);
                else
                    node = builder.createBool(false);
            } else if (nodeType == NodeType.NUMBER){
                node = builder.createNumber(parseDouble(nodeDataStr));
            } else if (nodeType == NodeType.STRING){
                node = builder.createString(nodeDataStr);
            } else {
                node = builder.create(nodeType).commit();
                replacementTable.put(nodeName, node);
                for (String linkName : links.keySet()) {
                    LinkType linkType = LinkType.valueOf(linkName.toUpperCase());
                    Object obj = links.get(linkName);
                    if (obj instanceof ArrayList) {
                        for (Object item : (ArrayList) obj)
                            if (item instanceof String)
                                setLink(builder, node, linkType, (String) item, nodes, replacementTable);
                    } else {
                        String objStr = obj instanceof String ? (String) obj : String.valueOf(obj);
                        setLink(builder, node, linkType, objStr, nodes, replacementTable);
                    }
                }
            }
        }
        return node;
    }

    public static Node fromMap(Map<String, Map<String, Object>> nodes) {
        return nodes.size() == 0 ? null :
                parseNodeRecursive(new NodeBuilder(), nodes.entrySet().iterator().next().getKey(), nodes, new HashMap<>());
    }

    public static Node[] fromList(List<Map<String, Map<String, Object>>> args) {
        List<Node> result = new ArrayList<>();
        for (Map<String, Map<String, Object>> map : args)
            result.add(fromMap(map));
        return result.toArray(new Node[0]);
    }

    public static Node fromJson(String response) {
        Type mapType = new TypeToken<Map<String, Map>>(){}.getType();
        Map<String, Map<String, Object>> responseObj = json.fromJson(response, mapType);
        return fromMap(responseObj);
    }

    static String streamToString(InputStream inputStream) {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static Node fromStream(InputStream inputStream) {
        // TODO add code parse like stream
        return fromJson(streamToString(inputStream));
    }
}
