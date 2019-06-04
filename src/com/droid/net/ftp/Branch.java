package com.droid.net.ftp;

import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;
import com.droid.djs.node.NodeUtils;

public class Branch {

    private NodeBuilder builder = new NodeBuilder();
    private Node newBranch = null;

    public Node create() {
        newBranch = builder.create().commit();
        Master.getInstance();
        builder.addLocal(newBranch);
        return newBranch;
    }

    public void mergeWithMaster() {
        Node master = Master.getInstance();
        NodeUtils.forEach(newBranch, branchNode -> {
            String path = NodeUtils.getPath(branchNode);
            Node masterNode = NodeUtils.putNode(master, path);
            builder.set(masterNode).addToHistory();
            masterNode.parse(branchNode.build());
            builder.commit();
        });
        deleteBranch();
    }

    public void deleteBranch() {
        builder.removeLocal(newBranch);
        builder.commit();
    }

}
