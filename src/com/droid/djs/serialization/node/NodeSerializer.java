package com.droid.djs.serialization.node;

import com.droid.djs.DataStorage;
import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.consts.LinkType;
import com.droid.djs.consts.NodeType;
import com.droid.djs.fs.Branch;
import com.droid.djs.nodes.DataInputStream;
// TODO remove Gson library
import com.droid.net.ftp.DataOutputStream;
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

*/
public class NodeSerializer {

    public static final String NEW_NODE_PREFIX = "w";
    public static final String NODE_PREFIX = "n";
    public static final String TYPE_PREFIX = "type";
    public static final String DATA_PREFIX = "data";
    public static final String STRING_PREFIX = "!";
    public static final String LINK_PREFIX = "@";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Node node) {
        return toJson(node, 15);
    }

    public static String toJson(Node node, int level) {
        return json.toJson(toMap(node, level));
    }

    public static Map<String, Map<String, Object>> toMap(Node node, int level) {
        Map<String, Map<String, Object>> data = new LinkedHashMap<>();
        toMapRecursive(new NodeBuilder(), data, level, node);
        return data;
    }

    private static Object dataSimplification(NodeBuilder builder, Node node) {
        DataInputStream dataInputStream = builder.set(node).getData();
        if (node.type == NodeType.STRING) {
            if (dataInputStream.length > DataStorage.MAX_STORAGE_DATA_IN_DB)
                return LINK_PREFIX + node.id;
            else
                return STRING_PREFIX + String.valueOf(dataInputStream.readChars());
        } else {
            return dataInputStream.getObject();
        }
    }

    public static void toMapRecursive(NodeBuilder builder, Map<String, Map<String, Object>> data, int depth, Node node) {
        if (node.id == 0) return;
        String nodeName = NODE_PREFIX + node.id;
        if (data.get(nodeName) != null) return;


        Map<String, Object> links = new LinkedHashMap<>();
        data.put(nodeName, links);

        if (node.type != NodeType.NODE)
            links.put(TYPE_PREFIX, node.type.toString());

        if (node.type.ordinal() < NodeType.NODE.ordinal())
            links.put(DATA_PREFIX, dataSimplification(builder, node));

        node.listLinks((linkType, link, singleValue) -> {
            if (linkType == LinkType.LOCAL_PARENT) return;

            if (linkType == LinkType.NATIVE_FUNCTION_NUMBER) {
                String linkTypeStr = linkType.toString().toLowerCase();
                links.put(linkTypeStr, "" + link);
                return;
            }
            // Nodes links
            Node linkNode = link instanceof Long ? builder.get((Long) link).getNode() : (Node) link;
            String linkTypeStr = linkType.toString().toLowerCase();
            if (singleValue) {
                if (linkNode.type.ordinal() < NodeType.NODE.ordinal())
                    links.put(linkTypeStr, dataSimplification(builder, linkNode));
                else if (depth > 0) {
                    links.put(linkTypeStr, NODE_PREFIX + linkNode.id);
                    toMapRecursive(builder, data, depth - 1, linkNode);
                }
            } else {
                Object linkObject = links.get(linkTypeStr);
                if (linkObject == null)
                    links.put(linkTypeStr, linkObject = new ArrayList<>());
                ArrayList linkList = (ArrayList) linkObject;

                if (linkNode.type.ordinal() < NodeType.NODE.ordinal()) // TODO exception
                    linkList.add(dataSimplification(builder, linkNode));
                else {
                    linkList.add(NODE_PREFIX + linkNode.id);
                    toMapRecursive(builder, data, depth, linkNode);
                }
            }
        });
    }

}
