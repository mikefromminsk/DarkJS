package com.droid.djs.fs;

import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.nodes.Data;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.ThreadNode;
import com.droid.instance.Instance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class Files {

    public static String getPath(Node file) {
        NodeBuilder builder = new NodeBuilder().set(file);
        StringBuilder path = new StringBuilder();
        Node master = Instance.get().getMaster();
        while (builder.getLocalParentNode() != null && builder.getNode() != master) {
            path.insert(0, "/" + builder.getTitleString());
            Node localParent = builder.getLocalParentNode();
            builder.set(localParent);
        }
        return "".equals(path.toString()) ? "/" : path.toString();
    }

    public static Node getNode(String path) {
        return getNode(path, NodeType.NODE);
    }

    public static Node getNodeIfExist(String path) {
        return getNode(path, null);
    }

    public static Node getNodeIfExist(Node root, String path) {
        return getNode(root, path, null);
    }

    public static Node getNodeIfExist(String uri, Long access_token) {
        return getNode(Instance.get().getMaster(), uri, null, access_token);
    }

    public static Node getNodeFromRoot(String uri) {
        return getNode(new NodeBuilder().get(0L).getNode(), uri);
    }

    public static Node getNodeFromRoot(String uri, NodeType nodeType) {
        return getNode(new NodeBuilder().get(0L).getNode(), uri, nodeType);
    }

    public static Node getNodeFromRootIfExist(String uri) {
        return getNodeIfExist(new NodeBuilder().get(0L).getNode(), uri);
    }

    public static Node getNode(String path, NodeType nodeType) {
        return getNode(Instance.get().getMaster(), path, nodeType);
    }

    public static Node getNode(Node root, String path) {
        return getNode(root, path, NodeType.NODE);
    }

    public static Node getNode(Node root, String path, NodeType nodeType) {
        return getNode(root, path, nodeType, null);
    }

    public static Node getNode(String path, NodeType nodeType, Long access_token) {
        return getNode(Instance.get().getMaster(), path, nodeType, access_token);
    }

    public static Node getNode(Node root, String path, NodeType nodeType, Long access_token) {
        NodeBuilder builder = new NodeBuilder().set(root);
        NodeBuilder builder2 = new NodeBuilder();
        // TODO remove types from names
        // TODO access to root
        // TODO add escape characters /
        if (path != null && !path.equals("")) {
            String[] names = path.split("/");
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                if (name.equals("")) continue;
                String type = null;
                if (name.contains(".")) {
                    int dotPos = name.indexOf('.');
                    type = name.substring(dotPos + 1);
                    name = name.substring(0, dotPos);
                }

                boolean find = false;
                for (Node node : builder.getLocalNodes()) {
                    String title = builder2.set(node).getTitleString();
                    if (name.equals(title)) {
                        builder.set(node);
                        if (builder.isThread() && access_token != null && !((ThreadNode) node).checkAccess(access_token))
                            return null;
                        find = true;
                        break;
                    }
                }

                if (!find) {
                    if (nodeType != null) {
                        boolean isTheLast = i == names.length - 1;
                        Node title = builder2.create(NodeType.STRING).setData(name).commit();
                        Node node = builder2.create(isTheLast ? nodeType : NodeType.NODE).setTitle(title).commit();
                        if (isTheLast) {
                            if (nodeType == NodeType.THREAD)
                                builder2.setOwnerAccessCode(access_token);
                            if (type != null) {
                                Data typeTitle = (Data) builder2.create(NodeType.STRING).setData(type).commit();
                                builder2.set(node).setParser(typeTitle).commit();
                            }
                        }
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
        return putFile(Instance.get().getMaster(), path, data);
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
        NodeBuilder builder = new NodeBuilder();
        Node dataNode = builder.create(NodeType.STRING).setData(stream).commit();
        builder.set(fileNode).setValue(dataNode).commit();
        return fileNode;
    }

    public static String getFileString(Node node, String path) {
        Node fileNode = getNode(node, path);
        NodeBuilder builder = new NodeBuilder();
        Node value = builder.set(fileNode).getValueNode();
        return builder.set(value).getData().readString();
    }

    public static boolean isDirectory(Node node) {
        return (node == null || new NodeBuilder().set(node).getParserNode() == null);
    }

    public static void replace(Node from, Node to) {
        NodeBuilder builder = new NodeBuilder();
        Node masterLocalParent = builder.set(from).getLocalParentNode();
        Node[] masterParentLocals = builder.set(masterLocalParent).getLocalNodes();
        int localIndex = Arrays.asList(masterParentLocals).indexOf(from);
        builder.set(masterLocalParent).setLocalNode(localIndex, to).commit();

        Node title = builder.set(from).getTitleNode();
        Data parser = builder.set(from).getParserNode();
        builder.set(to).setTitle(title).setParser(parser).commit();
    }

    public static void putNode(Node root, String path, Node node) {
        Node newNode = Files.getNode(root, path, node.type);
        replace(newNode, node);
    }

    public static void remove(Node root) {
        NodeBuilder builder = new NodeBuilder();
        Node masterLocalParent = builder.set(root).getLocalParentNode();
        builder.set(masterLocalParent).removeLocal(root);
    }

    public interface FindFile {
        void file(Node node);
    }

    public static void observe(String path, FindFile findFile) {
        Node node = getNodeIfExist(path);
        if (node != null)
            observeRec(new NodeBuilder(), node, findFile);
    }

    private static void observeRec(NodeBuilder builder, Node node, FindFile findFile) {
        Node[] locals = builder.set(node).getLocalNodes();
        for (Node local : locals){
            findFile.file(local);
            observeRec(builder, local, findFile);
        }
    }
}
