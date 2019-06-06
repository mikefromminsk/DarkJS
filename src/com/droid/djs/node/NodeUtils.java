package com.droid.djs.node;

import com.droid.net.ftp.Master;

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

    public static Node getStyle(Node node, String styleKey) {
        NodeBuilder builder = new NodeBuilder();
        for (Node style : builder.set(node).getStyleNodes()) {
            if (builder.set(style).getTitleString().equals(styleKey))
                return builder.getValueNode();
        }
        return null;
    }

    public static String getPath(Node file) {
        NodeBuilder builder = new NodeBuilder().set(file);
        String path = "";
        while (builder.getLocalParentNode().id != 0L) {
            path = "/" + builder.getTitleString() + path;
            Node localParent = builder.getLocalParentNode();
            builder.set(localParent);
        }
        return "".equals(path) ? "/" : path;
    }

    public static Node getNode(Node root, String path) {
        return getNode(root, path, true);
    }

    public static Node getNode(Node root, String path, Boolean makeDirs) {
        NodeBuilder builder = new NodeBuilder().set(root);
        NodeBuilder builder2 = new NodeBuilder();
        // TODO add escape characters /
        if (!"".equals(path))
            for (String name : path.split("/")) {
                if (name.equals("")) continue;
                boolean find = false;
                for (Node node : builder.getLocalNodes()) {
                    if (name.equals(builder2.set(node).getTitleString())) {
                        builder.set(node);
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    if (makeDirs) {
                        Node title = builder2.create(NodeType.STRING).setData(name).commit();
                        Node node = builder2.create().setTitle(title).commit();
                        builder.addLocal(node).commit();
                        builder.set(node);
                    } else {
                        return null;
                    }
                }
            }
        return builder.getNode();
    }

    public static Node getNode(String path) {
        return getNode(path, true);
    }

    public static Node getNode(String path, Boolean makeDir) {
        return getNode(Master.getInstance(), path, makeDir);
    }

    public static Node putFile(String path, String data) {
        return putFile(0L, path, data);
    }

    public static Node putFile(String path, InputStream data) {
        return putFile(Master.getInstance(), path, data);
    }

    public static Node putFile(Long nodeId, String path, String data) {
        return putFile(new NodeBuilder().get(nodeId).getNode(), path, new ByteArrayInputStream(data.getBytes()));
    }

    public static Node putFile(Node node, String path, String data) {
        return putFile(node, path, new ByteArrayInputStream(data.getBytes()));
    }

    public static Node putFile(Node node, String path, InputStream stream) {
        Node fileNode = getNode(node, path);
        NodeBuilder builder = new NodeBuilder();
        Node dataNode = builder.create(NodeType.STRING).setData(stream).commit();
        builder.set(fileNode).setValue(dataNode).commit();
        setStyle(fileNode, NodeStyle.SOURCE_CODE, dataNode);
        return fileNode;
    }

    public static String getFileString(Node node, String path) {
        Node fileNode = getNode(node, path);
        NodeBuilder builder = new NodeBuilder();
        Node value = builder.set(fileNode).getValueNode();
        return builder.set(value).getData().readString();
    }

    public interface FindFile {
        void find(Node node);
    }

    public static void forEachFiles(Node node, FindFile func) {
        NodeBuilder builder = new NodeBuilder().set(node);
        if (getStyle(node, NodeStyle.SOURCE_CODE) != null)
            func.find(node);
        for (Node local : builder.getLocalNodes())
            forEachFiles(local, func);
    }
}
