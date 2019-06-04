package com.droid.net.ftp;

import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;
import com.droid.djs.node.NodeUtils;

public class Branch {

    NodeBuilder builder = new NodeBuilder();
    Node newBranch = null;

    public Node create() {
        newBranch = builder.create().commit();
        builder.get(0L);
        builder.addLocal(newBranch);
        return newBranch;
    }

    public void mergeWithMaster() {
        NodeUtils.forEach(newBranch, node -> {

        });
        deleteBranch();
    }

    public void deleteBranch() {
        builder.removeLocal(newBranch);
        builder.commit();
    }

}
