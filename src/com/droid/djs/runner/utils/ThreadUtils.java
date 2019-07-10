package com.droid.djs.runner.utils;

import com.droid.djs.nodes.consts.NodeType;

public class ThreadUtils extends Utils {

    @Override
    public String name() {
        return "Thread";
    }

    @Override
    public void methods() {
        func("sleep", (builder, node, ths) -> {
            Object left = leftObject(builder, node);
            if (left instanceof Double) {
                try {
                    Thread.sleep((long) (double) left);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }, par("delay", NodeType.NUMBER));
    }
}
