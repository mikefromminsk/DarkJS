package com.droid.djs.fs;

import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.instance.Instance;

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
            Instance.get().getNodeStorage().transactionCommit();
            updateTimer();
        }
        return root;
    }

    public void updateTimer() {
        // TODO restart timer if after schedule event time is not up
        if (mergeTimer != 0) {
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
        if (builder.set(node).getLocalCount() == 1 && !builder.isFunction())
            return findPackage(builder.getLocalNode(0));
        else
            return node;
    }

    // TODO issue when in run folder only one file
    public void mergeWithMaster() {
        if (timer != null)
            timer.cancel();
        if (root != null) {
            Node branchPackage = findPackage(root);
            if (branchPackage != null) {
                String branchRootPath = Files.getPath(root);
                String branchFilePath = Files.getPath(branchPackage);
                branchFilePath = branchFilePath.substring(branchRootPath.length());
                Node masterPackage = Files.getNode(branchFilePath);
                Files.replace(masterPackage, branchPackage);
                if (masterPackage == Instance.get().getMaster())
                    Instance.get().removeMaster();
            }
            root = null;
            Instance.get().getNodeStorage().transactionCommit();
        }
    }
}
