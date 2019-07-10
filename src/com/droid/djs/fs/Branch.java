package com.droid.djs.fs;

import com.droid.djs.NodeStorage;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;

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
            builder.get(0L).addLocal(root).commit();
            NodeStorage.getInstance().transactionCommit();
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
        }, mergeTimer);
    }

    public Node findPackage(Node node) {
        // if file
        if (builder.set(node).getSourceCodeNode() != null)
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
        if (timer != null)
            timer.cancel();
        if (root != null) {
            Node branchPackage = findPackage(root);
            if (branchPackage != null) {
                Node masterPackage = Files.getNode(Master.getInstance(), Files.getPath(branchPackage));
                Files.replace(masterPackage, branchPackage);
                if (masterPackage == Master.getInstance())
                    Master.removeInstance();
                //builder.set(branchPackage).setHistory(masterPackage).commit();
            }
            if (root != Master.getInstance())
                builder.get(0L).removeLocal(root).commit();
            root = null;
            NodeStorage.getInstance().transactionCommit();
        }
    }
}
