package com.droid.djs.serialization.node;

import com.droid.djs.DataStorage;
import com.droid.djs.nodes.consts.LinkType;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.nodes.DataInputStream;
// TODO remove Gson library
import com.droid.djs.runner.utils.FuncInterface;
import com.droid.djs.runner.utils.Utils;
import com.droid.djs.serialization.json.JsonSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.droid.djs.nodes.*;

import java.util.*;

/*
* {
  "n1": {
    // exept true false
    "path": "/reverse2",
    "local": "!wefwef"
  },
  "n2": {
    "start": "/root/dev",
    "path": "/reverse",
    "name": "!title",
    "local": ["n1"],
    "data": "reverse",
    "type": "Object",
    "string": "!new title",
    "number": 30,
    "bool": true,
    "link": "n1",
    "array": [
      "n1",
      "n2",
      true,
      30
    ]
  }
}

/* build priority
        node_path
        node_local
        type
        data

*/
public class NodeSerializer {

    public static final String NEW_NODE_PREFIX = "w";
    public static final String NODE_PREFIX = "n";
    public static final String TYPE_KEY = "type";
    public static final String DATA_KEY = "data";
    public static final String STRING_PREFIX = "!";
    public static final String LINK_PREFIX = "@";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Node node) {
        return toJson(node, 2);
    }

    public static String toJson(Node node, int level) {
        return json.toJson(toMap(node, level));
    }

    public static Map<String, Map<String, Object>> toMap(Node node) {
        return toMap(node, -1);
    }

    public static Map<String, Map<String, Object>> toMap(Node node, int level) {
        if (level <= 0) level = 15;
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
                return STRING_PREFIX + dataInputStream.readString();
        } else {
            return dataInputStream.getObject();
        }
    }

    public static void toMapRecursive(NodeBuilder builder, Map<String, Map<String, Object>> data, int level, Node node) {
        if (node == null || node.id == 0) return;
        String nodeName = NODE_PREFIX + node.id;
        if (data.get(nodeName) != null) return;

        Map<String, Object> links = new LinkedHashMap<>();
        data.put(nodeName, links);

        if (node.type != NodeType.NODE)
            links.put(TYPE_KEY, node.type.toString().toLowerCase());

        if (node.type.ordinal() < NodeType.NODE.ordinal())
            links.put(DATA_KEY, dataSimplification(builder, node));

        node.listLinks((linkType, link, singleValue) -> {
            if (linkType == LinkType.LOCAL_PARENT) return;

            if (linkType == LinkType.NATIVE_FUNCTION) {
                String linkTypeStr = linkType.toString().toLowerCase();
                FuncInterface funcInterface = Utils.getFunctionInterface((int) (long) link);
                links.put(linkTypeStr, "!" + funcInterface.path + funcInterface.name);
                return;
            }
            // Nodes links
            Node linkNode = link instanceof Long ? builder.get((Long) link).getNode() : (Node) link;
            String linkTypeStr = linkType.toString().toLowerCase();
            if (singleValue) {
                if (linkNode.type.ordinal() < NodeType.NODE.ordinal())
                    links.put(linkTypeStr, dataSimplification(builder, linkNode));
                else if (level > 0) {
                    links.put(linkTypeStr, NODE_PREFIX + linkNode.id);
                    toMapRecursive(builder, data, level - 1, linkNode);
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
                    toMapRecursive(builder, data, level, linkNode);
                }
            }
        });
    }

    public static List<Map<String, Map<String, Object>>> toList(Node[] args) {
        List<Map<String, Map<String, Object>>> result = new ArrayList<>();
        for (Node arg : args)
            result.add(toMap(arg));
        return result;
    }

    private static String convertExtensionToMimeType(String extension) {
        switch (extension) {
            case "js":
                return "text/javascript";
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "png":
                return "image/png";
            default:
                return null;
        }
    }

    public static HttpResponse getResponse(Node node) {
        NodeBuilder builder = new NodeBuilder().set(node);
        if (builder.getNode() == null)
            return new HttpResponse("application/json", "null");

        String parser = builder.getParserString();
        if (parser != null)
            switch (parser) {
                case "json":
                    return new HttpResponse("application/json", JsonSerializer.serialize(builder));
                case "node.js":
                    return new HttpResponse("application/json", NodeSerializer.toJson(builder.getNode()));
                default: // node is static file
                    if (builder.getValueNode() instanceof Data)
                        return new HttpResponse(convertExtensionToMimeType(parser), ((Data) builder.getValueNode()).data.readBytes());
                    else
                        return new HttpResponse(convertExtensionToMimeType(parser), "");
            }
        return new HttpResponse("application/json", NodeSerializer.toJson(builder.getNode()));
    }
}
