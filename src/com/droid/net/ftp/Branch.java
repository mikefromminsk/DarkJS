package com.droid.net.ftp;

import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;
import com.droid.djs.node.NodeUtils;

public class Branch {

    NodeBuilder builder = new NodeBuilder();
    Node newBranch = null;

    public Node create() {
        newBranch = builder.create().commit();
        Master.getInstance();
        builder.addLocal(newBranch);
        return newBranch;
    }

    public void mergeWithMaster() {
        Node master = Master.getInstance();
        NodeUtils.forEach(newBranch, node -> {
            String path = NodeUtils.getPath(node);

        });
        deleteBranch();
    }

    public void deleteBranch() {
        builder.removeLocal(newBranch);
        builder.commit();
    }

}
