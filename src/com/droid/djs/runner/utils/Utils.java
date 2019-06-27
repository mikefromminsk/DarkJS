package com.droid.djs.runner.utils;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.consts.NodeType;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.NativeNode;
import com.droid.djs.nodes.Node;
import com.droid.djs.runner.Func;
import com.droid.djs.runner.prototypes.StringPrototype;
import com.droid.djs.serialization.node.Serializer;

import java.util.ArrayList;
import java.util.List;

abstract public class Utils {

    private static NodeBuilder builder = new NodeBuilder();
    public static String DEFAULT_PROTOTYPES_DIR = "defaultPrototypes/";
    public static Node defaultPrototypesDir;
    public static List<Func> functions = new ArrayList<>();
    public static Node trueValue = builder.create(NodeType.BOOL).setData(Serializer.TRUE).commit();
    public static Node falseValue = builder.create(NodeType.BOOL).setData(Serializer.FALSE).commit();

    public static void init(){
        defaultPrototypesDir  =  Files.getNode(DEFAULT_PROTOTYPES_DIR);
        new ThreadUtils().methods();
        new MathUtils().methods();
        new StringPrototype().methods();
    }

    public void func(String name, Func func, Node... args) {
        String functionName = (name().endsWith("/") ? name() : name() + "/") + name;
        NativeNode function = (NativeNode) Files.getNode(functionName, NodeType.NATIVE_FUNCTION);
        functions.add(func);
        builder.set(function).setFunctionIndex(functions.size() - 1);
        for (Node arg : args)
            builder.addParam(arg);
        builder.commit();
    }

    public Node par(String name, NodeType nodeType) {
        Node title = builder.create(NodeType.STRING).setData(name).commit();
        Node defValue = null;
        if (nodeType == NodeType.NUMBER)
            defValue = builder.create(nodeType).setData(0D).commit();
        else if (nodeType == NodeType.STRING)
            defValue = builder.create(nodeType).setData("string").commit();
        else
            defValue = builder.create(nodeType).commit();
        return builder.create().setTitle(title).setValue(defValue).commit();
    }

    Object leftObject(NodeBuilder builder, Node node) {
        return toObject(builder, builder.set(node).getParamNode(0));
    }

    Object rightObject(NodeBuilder builder, Node node) {
        return toObject(builder, builder.set(node).getParamNode(1));
    }

    protected Object toObject(NodeBuilder builder, Node node) {
        builder.set(node);
        if (node != null) node = builder.set(node).getValueOrSelf();
        Object obj = null;
        if (node != null && node.type.ordinal() < NodeType.NODE.ordinal())
            obj = builder.set(node).getData().getObject();
        return obj;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public abstract String name();

    public abstract void methods();
}
