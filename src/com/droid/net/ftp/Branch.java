package com.droid.net.ftp;

import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;
import com.droid.djs.node.NodeUtils;

public class Branch {

    private NodeBuilder builder = new NodeBuilder();
    private Node root;

    public Branch() {
        root = builder.create().commit();
        builder.get(0L).addLocal(root);
    }

    public void mergeWithMaster() {
        Node master = Master.getInstance();
        NodeUtils.forEach(root, branchNode -> {
            String path = NodeUtils.getPath(branchNode);
            Node masterNode = NodeUtils.getNode(master, path);
            // TODO stop threads and start in new branch
            builder.set(masterNode).addToHistory();
            masterNode.parse(branchNode.build());
            builder.commit();
        });
        deleteBranch();
    }

    public void deleteBranch() {
        builder.get(0L).removeLocal(root).commit();
    }

    public Node getRoot() {
        return root;
    }
}
