package com.droid.djs.nodes;

import com.droid.djs.runner.Runner;

import java.util.LinkedList;
import java.util.Map;

public class ThreadNode extends Node implements Runnable {

    private Thread thread = new Thread(this);

    class RunData {
        Node node;
        Map<String, String> args;

        public RunData(Node node, Map<String, String> args) {
            this.node = node;
            this.args = args;
        }
    }

    private LinkedList<RunData> runQueue = new LinkedList<>();

    public void run(Node node) {
        if (thread.isAlive()){
            runQueue.add(new RunData(node, null));
        } else {
            thread.start();
        }
    }

    @Override
    public void run() {
        Runner runner = new Runner();
        while (runQueue.size() > 0) {
            RunData data = runQueue.pollFirst();
            if (data != null){
                runner.run(data.node);
            }
        }
    }

}
