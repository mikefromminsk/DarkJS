package com.droid.djs.runner.utils;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.consts.NodeType;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;

abstract public class Utils {

    private static NodeBuilder builder = new NodeBuilder();
    public static String DEFAULT_PROTOTYPES_DIR = "defaultPrototypes/";

    public void func(String functionName, int functionId) {
        Node function = Files.getNode((root().endsWith("/") ? root() : root() + "/") + functionName, NodeType.NATIVE_FUNCTION);
        builder.set(function).setFunctionId(functionId).commit();
    }

    public abstract String root();

    public abstract void init();
}
