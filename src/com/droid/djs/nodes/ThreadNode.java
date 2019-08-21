package com.droid.djs.nodes;

import com.droid.djs.nodes.consts.LinkType;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.runner.Runner;
import com.droid.instance.Instance;
import com.droid.instance.InstanceParameters;

import java.util.*;

public class ThreadNode extends Node implements Runnable {

    public Thread thread = new Thread(this);
    private Runner runner = new Runner();
    public Long access_owner = null;
    private ArrayList<Long> access_user = null;

    public ThreadNode() {
        super(NodeType.THREAD);
        Instance.get().getThreads().registration(thread);
        thread.start();
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

    class RunData {
        Boolean callerIsBlocked;
        InstanceParameters instanceParameters;
        Node node;
        Map<String, String> args;

        public RunData(Boolean callerIsBlocked, InstanceParameters instanceParameters, Node node, Map<String, String> args) {
            this.callerIsBlocked = callerIsBlocked;
            this.instanceParameters = instanceParameters;
            this.node = node;
            this.args = args;
        }
    }

    private LinkedList<RunData> runQueue = new LinkedList<>();

    public boolean run(Node node, Node[] args, boolean async, Long access_token) {
        System.out.println(Thread.currentThread().getId() + " run " + Instance.get().storeDir);
        if (!checkAccess(access_token))
            return false;

        setParams(node, args);

        RunData runData = new RunData(async ? null : true, Instance.get(), node, null);
        runQueue.add(runData);

        notify(runQueue);
        wait(runData.callerIsBlocked);

        return true;
    }

    private void setParams(Node node, Node[] args) {
        if (args != null) {
            NodeBuilder builder = new NodeBuilder();
            for (Node param : builder.set(node).getParams())
                builder.set(param).setValue(null);

            for (int i = 0; i < Math.min(args.length, builder.set(node).getParamCount()); i++) {
                Node destParam = builder.set(node).getParamNode(i);
                Node sourceParam = builder.set(args[i]).getValueOrSelf();
                builder.set(destParam).setValue(sourceParam).commit();
            }
        }
    }

    void notify(Object obj) {
        if (obj != null)
            synchronized (obj) {
                obj.notify();
            }
    }

    void wait(Object obj) {
        if (obj != null)
            synchronized (obj) {
                try {
                    obj.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void run() {
        // TODO create timer of ThreadNode live
        ThreadNode node = this;
        while (true) {
            System.out.println(Thread.currentThread().getId() + " run loop");
            while (!runQueue.isEmpty()) {
                RunData runData = runQueue.pollFirst();
                if (runData != null) {
                    Instance.connectThread(runData.instanceParameters);
                    runner.start(runData.node);
                    Instance.disconnectThread();
                    notify(runData.callerIsBlocked);
                } else
                    runQueue.clear();
            }
            wait(runQueue);
        }
    }

}
