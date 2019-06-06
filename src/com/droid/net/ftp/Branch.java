package com.droid.net.ftp;

import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;
import com.droid.djs.node.NodeStyle;
import com.droid.djs.node.NodeUtils;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Branch {

    private NodeBuilder builder = new NodeBuilder();
    private Node root;
    private Timer timer = new Timer();
    private int mergeTimer;

    public Branch() {
        this(2000);
    }

    public Branch(int mergeTimer) {
        this.mergeTimer = mergeTimer;
    }

    public Node getRoot() {
        if (root == null) {
            root = builder.create().commit();
            builder.get(0L).addLocal(root);
            updateTimer();
        }
        return root;
    }

    public void updateTimer() {
        // TODO restart timer if after schedule event time is not up
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mergeWithMaster();
            }
        }, 2000);
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
        if (root != null) {
            Node branchPackage = findPackage(root);
            if (branchPackage != null) {
                Node masterPackage = NodeUtils.getNode(Master.getInstance(), NodeUtils.getPath(branchPackage));
                Node localParent = builder.set(masterPackage).getLocalParentNode();
                Node[] locals = builder.set(localParent).getLocalNodes();
                int localIndex = Arrays.asList(locals).indexOf(masterPackage);
                builder.set(localParent).setLocalNode(localIndex, branchPackage).commit();
                if (masterPackage == Master.getInstance())
                    Master.removeInstance();
                //builder.set(branchPackage).setHistory(masterPackage).commit();
            }
            if (root != Master.getInstance())
                builder.get(0L).removeLocal(root).commit();
            root = null;
        }
    }
}
