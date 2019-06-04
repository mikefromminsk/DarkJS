package com.droid.djs.node;

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

    public static Node putNode(Node root, String path) {
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

    public static Node putNode(String path) {
        return putNode(new NodeBuilder().get(0L).getNode(), path);
    }

    public static Node putFile(String path, String data) {
        return putFile(0L, path, data);
    }

    public static Node putFile(String path, InputStream data) {
        return putFile(new NodeBuilder().get(0L).getNode(), path, data);
    }

    public static Node putFile(Long nodeId, String path, String data) {
        return putFile(new NodeBuilder().get(nodeId).getNode(), path, new ByteArrayInputStream(data.getBytes()));
    }

    public static Node putFile(Node node, String path, String data) {
        return putFile(node, path, new ByteArrayInputStream(data.getBytes()));
    }

    public static Node putFile(Node node, String path, InputStream stream) {
        Node fileNode = putNode(node, path);
        NodeBuilder builder = new NodeBuilder();
        Node dataNode = builder.create(NodeType.STRING).setData(stream).commit();
        builder.set(fileNode).setValue(dataNode).commit();
        setStyle(fileNode, NodeStyle.SOURCE_CODE, stream);
        return fileNode;
    }

    public interface FindFile {
        void find(Node node);
    }

    public static void forEach(Node node, FindFile func) {
        NodeBuilder builder = new NodeBuilder().set(node);
        func.find(node);
        for (Node local: builder.getLocalNodes())
            forEach(local, func);
    }
}
