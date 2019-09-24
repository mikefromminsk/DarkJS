package org.pdk.engine.convertors.node;

import org.pdk.engine.store.Storage;
import org.pdk.engine.consts.LinkType;
import org.pdk.engine.consts.NodeType;
import org.pdk.engine.store.nodes.DataInputStream;
// TODO remove Gson library
import org.pdk.engine.modules.utils.FuncInterface;
import org.pdk.engine.modules.utils.Module;
import org.pdk.engine.convertors.json.JsonSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.pdk.engine.store.nodes.*;

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
            if (dataInputStream.length > Storage.MAX_STORAGE_DATA_IN_DB)
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
                FuncInterface funcInterface = Module.getFunctionInterface((int) (long) link);
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


    public static HttpResponse getResponse(Node node) {
        NodeBuilder builder = new NodeBuilder().set(node);
        if (builder.getNode() == null)
            return new HttpResponse(HttpResponseType.NULL, "");

        String parser = builder.getParserString();
        if (parser != null) {
            parser = parser.toLowerCase();
            switch (parser) {
                case "json":
                    return new HttpResponse(HttpResponseType.JSON, JsonSerializer.serialize(builder));
                case "node.js":
                    return new HttpResponse(HttpResponseType.JSON, NodeSerializer.toJson(builder.getNode()));
                default: // node is static file
                    if (builder.getValueNode() instanceof Data)
                        return new HttpResponse(HttpResponseType.fromParserName(parser), ((Data) builder.getValueNode()).data.readBytes());
                    else
                        return new HttpResponse(HttpResponseType.fromParserName(parser), "");
            }
        }
        if (builder.isData()) {
            String contentType;
            if (builder.isNumber()) {
                contentType = HttpResponseType.NUMBER_BASE10;
            } else if (builder.isBoolean()) {
                contentType = HttpResponseType.BOOLEAN;
            } else {
                contentType = HttpResponseType.TEXT;
            }
            return new HttpResponse(contentType, ((Data) builder.getNode()).data.readString());
        }
        return new HttpResponse(HttpResponseType.JSON, NodeSerializer.toJson(builder.getNode()));
    }
}
