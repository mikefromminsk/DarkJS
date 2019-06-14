package com.droid.djs.serialization.links;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.consts.LinkType;
import com.droid.djs.consts.NodeType;
import com.droid.djs.NodeStorage;
import com.droid.djs.nodes.DataInputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.droid.djs.nodes.*;

import java.util.*;

/*
* {
  "node1": {
    // exept true false
    "path": "/reverse2",
    "local": "!wefwef"
  },
  "node2": {
    "start": "/root/dev",
    "path": "/reverse",
    "name": "!title",
    "local": ["node1"],
    "data": "reverse",
    "type": "Object",
    "string": "!new title",
    "number": 30,
    "bool": true,
    "link": "node1",
    "array": [
      "node1",
      "node2",
      true,
      30
    ]
  }
}

/* parse priority
        node_path
        node_local
        type
        data

*
*
* */
public class Formatter {

    public static final String NEW_NODE_PREFIX = "w";
    public static final String NODE_PREFIX = "n";
    public static final String TYPE_PREFIX = "type";
    public static final String DATA_PREFIX = "data";
    public static final String FUNCTION_ID_PREFIX = "function_id";
    public static final String STRING_PREFIX = "!";
    public static final String LINK_PREFIX = "@";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private static Object dataSimplification(NodeBuilder builder, Node node) {
        DataInputStream dataInputStream = builder.set(node).getData();
        if (node.type == NodeType.STRING) {
            if (dataInputStream.length > NodeStorage.MAX_STORAGE_DATA_IN_DB)
                return LINK_PREFIX + node.id;
            else
                return STRING_PREFIX + String.valueOf(dataInputStream.readChars());
        } else {
            return dataInputStream.getObject();
        }
    }

    public static void toJsonRecursive(NodeBuilder builder, Map<String, Map<String, Object>> data, int depth, Node node) {
        if (node.id == 0) return;
        String nodeName = NODE_PREFIX + node.id;
        if (data.get(nodeName) != null) return;


        Map<String, Object> links = new LinkedHashMap<>();
        data.put(nodeName, links);

        if (node.type != NodeType.VAR)
            links.put(TYPE_PREFIX, NodeType.toString(node.type));

        if (node.type < NodeType.VAR)
            links.put(DATA_PREFIX, dataSimplification(builder, node));

        node.listLinks((linkType, link, singleValue) -> {
            if (linkType == LinkType.LOCAL_PARENT) return;

            if (linkType == LinkType.NATIVE_FUNCTION_NUMBER) {
                String linkTypeStr = LinkType.toString(linkType);
                links.put(linkTypeStr, "" + link);
                return;
            }
            // Nodes links
            Node linkNode = link instanceof Long ? builder.get((Long) link).getNode() : (Node) link;
            String linkTypeStr = LinkType.toString(linkType);
            if (singleValue) {
                if (linkNode.type < NodeType.VAR)
                    links.put(linkTypeStr, dataSimplification(builder, linkNode));
                else if (depth > 0) {
                    links.put(linkTypeStr, NODE_PREFIX + linkNode.id);
                    toJsonRecursive(builder, data, depth - 1, linkNode);
                }
            } else {
                Object linkObject = links.get(linkTypeStr);
                if (linkObject == null)
                    links.put(linkTypeStr, linkObject = new ArrayList<>());
                ArrayList linkList = (ArrayList) linkObject;

                if (linkNode.type < NodeType.VAR) // TODO exception
                    linkList.add(dataSimplification(builder, linkNode));
                else {
                    linkList.add(NODE_PREFIX + linkNode.id);
                    toJsonRecursive(builder, data, depth, linkNode);
                }
            }
        });
    }

    public static Map<String, Map<String, Object>> toMap(Node node) {
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        toJsonRecursive(new NodeBuilder(), data, 15, node);
        return data;
    }

    // TODO delete Gson object
    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Node node) {
        return json.toJson(toMap(node));
    }


    private static void setLink(NodeBuilder builder, com.droid.djs.nodes.Node node, byte linkType, Map<String, com.droid.djs.nodes.Node> replacementTable, String itemStr) {
        com.droid.djs.nodes.Node linkValueNode = null;

        if (itemStr.equals(Formatter.TRUE) || itemStr.equals(Formatter.FALSE))
            linkValueNode = builder.create(NodeType.BOOL)
                    .setData(itemStr)
                    .commit();
        if (itemStr.charAt(0) >= '0' && itemStr.charAt(0) <= '9' || itemStr.charAt(0) == '-')
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
            if (nodeStr.startsWith(Formatter.NEW_NODE_PREFIX)) {
                com.droid.djs.nodes.Node node = builder.create().commit();
                request.replacements.put(nodeStr, Formatter.NODE_PREFIX + node.id);
                replacementTable.put(nodeStr, node);
            }
        }

        for (String nodeStr : request.nodes.keySet()) {
            com.droid.djs.nodes.Node node = replacementTable.get(nodeStr);
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
                    } else {
                        setLink(builder, node, linkType, replacementTable, "" + obj);
                    }
            }

            builder.set(node).commit();
        }
    }


}
