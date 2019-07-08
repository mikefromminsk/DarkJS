package com.droid.djs.nodes;

import com.droid.djs.consts.LinkType;
import com.droid.djs.consts.NodeType;
import com.droid.djs.runner.Runner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadNode extends Node implements Runnable {

    public Thread thread;
    private Runner runner = new Runner();
    public Long access_owner = null;
    private ArrayList<Long> access_user = null;


    public ThreadNode() {
        super(NodeType.THREAD);
    }

    @Override
    public void listLinks(NodeLinkListener linkListener) {
        super.listLinks(linkListener);
        if (access_owner != null)
            linkListener.get(LinkType.ACCESS_OWNER, access_owner, true);
        if (access_user != null)
            for (Long access_item : access_user)
                linkListener.get(LinkType.ACCESS_USER, access_item, false);
    }

    @Override
    void restore(LinkType linkType, long linkData) {
        super.restore(linkType, linkData);
        switch (linkType) {
            case ACCESS_OWNER:
                access_owner = linkData;
                break;
            case ACCESS_USER:
                if (access_user == null)
                    access_user = new ArrayList<>();
                access_user.add(linkData);
                break;
        }
    }

    class RunData {
        Node node;
        Map<String, String> args;

        public RunData(Node node, Map<String, String> args) {
            this.node = node;
            this.args = args;
        }
    }

    public boolean checkAccess(Long access_token) {
        boolean secure_enabled = access_owner != null || access_user != null;
        if (secure_enabled) {
            boolean access_granted = (access_owner != null && access_owner.equals(access_token))
                    || (access_user != null && access_user.indexOf(access_token) != -1);
            if (!access_granted)
                return false;
        }
        return true;
    }

    private LinkedList<RunData> runQueue = new LinkedList<>();

    public boolean run(Node node, boolean async, Long access_token) {
        if (!checkAccess(access_token))
            return false;

        runQueue.add(new RunData(node, null));
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this);
            thread.start();
        }
        if (!async) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (!runQueue.isEmpty()) {
            RunData data = runQueue.pollFirst();
            if (data != null)
                runner.start(data.node);
            else
                runQueue.clear();
        }
    }

}
