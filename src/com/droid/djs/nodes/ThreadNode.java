package com.droid.djs.nodes;

import com.droid.djs.consts.NodeType;
import com.droid.djs.runner.Runner;

import java.util.LinkedList;
import java.util.Map;

public class ThreadNode extends Node implements Runnable {

    public Thread thread;
    private Runner runner = new Runner();

    public ThreadNode() {
        super(NodeType.THREAD);
    }

    class RunData {
        Node node;
        Map<String, String> args;
        byte[] token;

        public RunData(Node node, Map<String, String> args, byte[] token) {
            this.node = node;
            this.args = args;
            this.token = token;
        }
    }

    private LinkedList<RunData> runQueue = new LinkedList<>();

    public void run(Node node, boolean async, byte[] token) {
        runQueue.add(new RunData(node, null, token));
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
    }

    @Override
    public void run() {
        while (runQueue.size() > 0) {
            RunData data = runQueue.pollFirst();
            runner.start(data.token, data.node);
        }
    }

}
