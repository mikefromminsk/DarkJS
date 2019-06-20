package com.droid.djs.fs;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.nodes.Node;
import com.droid.djs.consts.NodeStyle;
import com.droid.djs.consts.NodeType;
import com.droid.djs.nodes.ThreadNode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Files {

    public static Node setStyle(Node node, String key, String value) {
        return setStyle(node, key, new ByteArrayInputStream(value.getBytes()));
    }

    // TODO remove style and add FileNode with source code link type
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

    public static Node getNode(String path) {
        return getNode(path, NodeType.VAR);
    }

    public static Node getNode(String path, Byte nodeType) {
        return getNode(Master.getInstance(), path, nodeType);
    }

    public static Node getNode(Node root, String path) {
        return getNode(root, path, NodeType.VAR);
    }

    public static Node getNode(Node root, String path, Byte nodeType) {
        return getNode(root, path, nodeType, null);
    }

    public static Node getNode(String path, Byte nodeType, Long access_code) {
        return getNode(Master.getInstance(), path, nodeType, access_code);
    }

    public static Node getNode(Node root, String path, Byte nodeType, Long access_code) {
        NodeBuilder builder = new NodeBuilder().set(root);
        NodeBuilder builder2 = new NodeBuilder();
        // TODO add escape characters /
        if (path != null && !path.equals("")) {
            String[] names = path.split("/");
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                if (name.equals("")) continue;
                boolean find = false;
                for (Node node : builder.getLocalNodes()) {
                    if (name.equals(builder2.set(node).getTitleString())) {
                        builder.set(node);
                        if (builder.isThread() && access_code != null && !((ThreadNode) node).checkAccess(access_code))
                                return null;
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    if (nodeType != null) {
                        boolean isTheLast = i == names.length - 1;
                        Node title = builder2.create(NodeType.STRING).setData(name).commit();
                        Node node = builder2.create(isTheLast ? nodeType : NodeType.VAR).setTitle(title).commit();
                        if (isTheLast && nodeType == NodeType.THREAD)
                            builder2.setOwnerAccessCode(access_code);
                        builder.addLocal(node).commit();
                        builder.set(node);
                    } else {
                        return null;
                    }
                }
            }
        }
        return builder.getNode();
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

    public static Node putFile(Node node, String data) {
        return putFile(node, new ByteArrayInputStream(data.getBytes()));
    }

    public static Node putFile(Node node, InputStream stream) {
        return putFile(node, null, stream);
    }

    public static Node putFile(Node node, String path, InputStream stream) {
        Node fileNode = getNode(node, path);
        Node dataNode = setStyle(fileNode, NodeStyle.SOURCE_CODE, stream);
        new NodeBuilder().set(fileNode).setValue(dataNode).commit();
        return fileNode;
    }

    public static String getFileString(Node node, String path) {
        Node fileNode = getNode(node, path);
        NodeBuilder builder = new NodeBuilder();
        Node value = builder.set(fileNode).getValueNode();
        return builder.set(value).getData().readString();
    }

    public static boolean isDirectory(Node node) {
        return (node == null || Files.getStyle(node, NodeStyle.SOURCE_CODE) == null);
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
