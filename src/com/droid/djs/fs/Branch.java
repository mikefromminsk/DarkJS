package com.droid.djs.fs;

import com.droid.djs.NodeStorage;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Branch {

    private NodeBuilder builder = new NodeBuilder();
    private Node root;
    private Timer timer = new Timer();
    private Random random = new Random();
    private int mergeTimer;

    public Branch() {
        this(0);
    }

    public Branch(int mergeTimer) {
        this.mergeTimer = mergeTimer;
    }

    public Node getRoot() {
        if (root == null) {
            root = Files.getNode("Branch/" + Math.abs(random.nextInt()));
            NodeStorage.getInstance().transactionCommit();
            updateTimer();
        }
        return root;
    }

    public void updateTimer() {
        // TODO restart timer if after schedule event time is not up
        if (mergeTimer != 0){
            timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mergeWithMaster();
                }
            }, mergeTimer);
        }
    }

    public Node findPackage(Node node) {
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
            String branchRootPath = Files.getPath(root);
            if (branchPackage != null) {
                String branchFilePath = Files.getPath(branchPackage);
                branchFilePath = branchFilePath.substring(branchRootPath.length());
                Node masterPackage = Files.getNode(Master.getInstance(), branchFilePath);
                Files.replace(masterPackage, branchPackage);
                if (masterPackage == Master.getInstance())
                    Master.removeInstance();
            }
            if (root != Master.getInstance())
                Files.remove(root);
            root = null;
            NodeStorage.getInstance().transactionCommit();
        }
    }
}
