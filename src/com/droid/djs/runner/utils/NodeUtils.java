package com.droid.djs.runner.utils;

import com.droid.djs.consts.NodeType;

public class NodeUtils extends Utils {

    public static String GET_NAME = "get";
    public static final int GET = 24;

    public static String PATH_NAME = "path";
    public static final int PATH = 25;

    @Override
    public String name() {
        return "Node";
    }

    @Override
    public void methods() {
        func(GET_NAME, GET, par("url", NodeType.STRING), par("args", NodeType.OBJECT));
        func(PATH_NAME, PATH, par("url", NodeType.STRING), par("args", NodeType.OBJECT));
    }
}
