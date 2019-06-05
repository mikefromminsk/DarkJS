package com.droid.net.ftp;

import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;
import com.droid.djs.node.NodeStyle;
import com.droid.djs.node.NodeUtils;

import java.util.Arrays;

public class Branch {

    private NodeBuilder builder = new NodeBuilder();
    private Node root;

    public Branch() {
        root = builder.create().commit();
        builder.get(0L).addLocal(root);
    }

    public Node findPackage(Node node) {
        // if file
        if (NodeUtils.getStyle(node, NodeStyle.SOURCE_CODE) != null)
            return node;
        // if package
        if (builder.set(node).getLocalCount() > 1)
            return node;
        else if (builder.getLocalCount() == 0)
            return null;
        else
            return findPackage(builder.getLocalNode(0));
    }

    public void mergeWithMaster() {
        Node branchPackage = findPackage(root);
        if (branchPackage != null){
            Node masterPackage = NodeUtils.getNode(Master.getInstance(), NodeUtils.getPath(branchPackage));
            Node localParent = builder.set(masterPackage).getLocalParentNode();
            Node[] locals = builder.set(localParent).getLocalNodes();
            int localIndex = Arrays.asList(locals).indexOf(masterPackage);
            builder.set(localParent).setLocalNode(localIndex, branchPackage).commit();
            builder.set(branchPackage).setHistory(masterPackage).commit();
        }
        deleteBranch();
    }

    public void deleteBranch() {
        builder.get(0L).removeLocal(root).commit();
    }

    public Node getRoot() {
        return root;
    }
}
