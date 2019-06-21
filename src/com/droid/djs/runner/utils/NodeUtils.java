package com.droid.djs.runner.utils;

import com.droid.djs.consts.NodeType;

public class NodeUtils extends Utils {

    public static final int GET = 24;
    public static final int PATH = 25;

    @Override
    public String name() {
        return "Node";
    }

    @Override
    public void methods() {
        func("get", GET, par("url", NodeType.STRING), par("args", NodeType.OBJECT));
        func("path", PATH, par("url", NodeType.STRING), par("args", NodeType.OBJECT));
    }
}
