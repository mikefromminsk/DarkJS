package com.metabrain.djs.node;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class NodeUtils {

    public static Node setStyle(Node node, String key, String value) {
        return setStyle(node, key, new ByteArrayInputStream(value.getBytes()));
    }

    public static Node setStyle(Node node, String key, InputStream value) {
        return setStyle(node, key, new NodeBuilder().create(NodeType.STRING).setData(value).commit());
    }

    public static Node setStyle(Node node, String key, Node valueNode) {
        NodeBuilder builder = new NodeBuilder();

        Node keyNode = builder.create(NodeType.STRING).setData(key).commit();
        Node nodeStyle = builder.set(node).findStyle(keyNode.id);
        if (nodeStyle == null) {
            Node sourceCodeNode = builder.create(NodeType.VAR).setTitle(keyNode).setValue(valueNode).commit();
            builder.set(node).addStyle(sourceCodeNode).commit();
        } else {
            builder.set(nodeStyle).setValue(valueNode).commit();
        }
        return valueNode;
    }

    public static String getPath(Node file) {
        NodeBuilder builder = new NodeBuilder().set(file);
        String path = "";
        while (builder.getLocalParent() != null) {
            path += "/" + builder.getTitleString();
            Node localParent = builder.getLocalParentNode();
            builder.set(localParent);
        }
        return path;
    }

    public static Node putPath(Node root, String path) {
        NodeBuilder builder = new NodeBuilder().set(root);
        NodeBuilder builder2 = new NodeBuilder();
        // TODO add escape characters /
        if (!"".equals(path))
            for (String name : path.split("/")) {
                boolean find = false;
                for (Node node : builder.getLocalNodes()) {
                    if (name.equals(builder2.set(node).getTitleString())) {
                        builder.set(node);
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    Node title = builder2.create(NodeType.STRING).setData(name).commit();
                    Node node = builder2.create().setTitle(title).commit();
                    builder.addLocal(node).commit();
                    builder.set(node);
                }
            }
        return builder.getNode();
    }
}
