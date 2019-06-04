package com.droid.net.ftp;

import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;
import com.droid.djs.node.NodeUtils;

public class Branch {

    private NodeBuilder builder = new NodeBuilder();
    private Node root = null;

    public Node createBranch() {
        root = builder.create().commit();
        Master.getInstance();
        builder.addLocal(root);
        return root;
    }

    public void mergeWithMaster() {
        Node master = Master.getInstance();
        NodeUtils.forEach(root, branchNode -> {
            String path = NodeUtils.getPath(branchNode);
            Node masterNode = NodeUtils.putNode(master, path);
            // TODO stop threads and start in new branch
            builder.set(masterNode).addToHistory();
            masterNode.parse(branchNode.build());
            builder.commit();
        });
        deleteBranch();
    }

    public void deleteBranch() {
        builder.removeLocal(root);
        builder.commit();
    }

    public Node getRoot() {
        return root;
    }
}
