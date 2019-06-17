package com.droid.djs.runner.utils;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.consts.NodeType;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.NativeNode;
import com.droid.djs.nodes.Node;

abstract public class Utils {

    private static NodeBuilder builder = new NodeBuilder();
    public static String DEFAULT_PROTOTYPES_DIR = "defaultPrototypes/";

    public void func(String name, int functionId, Node... args) {
        NativeNode function = (NativeNode) Files.getNode((root().endsWith("/") ? root() : root() + "/") + name, NodeType.NATIVE_FUNCTION);
        builder.set(function).setFunctionId(functionId);
        for (Node arg: args)
            builder.addParam(arg);
        builder.commit();
    }

    public Node par(String name, byte nodeType) {
        Node title = builder.create(NodeType.STRING).setData(name).commit();
        Node defValue = null;
        if (nodeType == NodeType.NUMBER)
            defValue = builder.create(nodeType).setData(0D).commit();
        else if (nodeType == NodeType.STRING)
            defValue = builder.create(nodeType).setData("").commit();
        else
            defValue = builder.create(nodeType).commit();
        return builder.create().setTitle(title).setValue(defValue).commit();
    }

    public abstract String root();

    public abstract void init();
}
