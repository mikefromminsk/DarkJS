package com.droid.djs.runner.utils;

import com.droid.djs.serialization.node.NodeBuilder;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.NativeNode;
import com.droid.djs.nodes.Node;
import com.droid.djs.runner.Func;
import com.droid.djs.runner.prototypes.StringPrototype;
import com.droid.djs.serialization.node.NodeSerializer;

import java.util.*;

abstract public class Utils {

    private static NodeBuilder builder = new NodeBuilder();
    public static String DEFAULT_PROTOTYPES_DIR = "defaultPrototypes/";
    private static Node defaultPrototypesDir;
    private static List<Func> functions = new ArrayList<>();
    private static List<FuncInterface> interfaces = new ArrayList<>();

    public static Node trueValue = builder.create(NodeType.BOOL).setData(NodeSerializer.TRUE).commit();
    public static Node falseValue = builder.create(NodeType.BOOL).setData(NodeSerializer.FALSE).commit();

    public Utils() {
        methods();
    }

    public static List<Func> getFunctions() {
        if (functions.size() == 0) {
            new StringPrototype();
            new ThreadUtils();
            new MathUtils();
            new RootUtils();
            new NodeUtils();
        }
        return functions;
    }

    public static Node getDefaultPrototypesDir() {
        if (defaultPrototypesDir == null)
            defaultPrototypesDir = Files.getNode(DEFAULT_PROTOTYPES_DIR);
        return defaultPrototypesDir;
    }

    public void func(String name, Func func, Parameter... args) {
        functions.add(func);
        interfaces.add(new FuncInterface((name().endsWith("/") ? name() : name() + "/"), name, Arrays.asList(args)));
    }

    public static void saveInterfaces() {
        for (int i = 0; i < interfaces.size(); i++) {
            FuncInterface funcInterface = interfaces.get(i);
            String functionName = funcInterface.path + funcInterface.name;
            NativeNode function = (NativeNode) Files.getNode(functionName, NodeType.NATIVE_FUNCTION);
            builder.set(function).setFunctionIndex(i);
            for (Parameter parameter : funcInterface.parameters)
                builder.addParam(parNode(parameter));
            builder.commit();
        }
    }

    private static Node parNode(Parameter parameter) {
        Node title = builder.create(NodeType.STRING).setData(parameter.name).commit();
        Node defValue = null;
        if (parameter.nodeType == NodeType.NUMBER)
            defValue = builder.create(parameter.nodeType).setData(0D).commit();
        else if (parameter.nodeType == NodeType.STRING)
            defValue = builder.create(parameter.nodeType).setData("string").commit();
        else
            defValue = builder.create(parameter.nodeType).commit();
        return builder.create().setTitle(title).setValue(defValue).commit();
    }

    public Parameter par(String name, NodeType nodeType) {
        return new Parameter(name, nodeType);
    }

    Object leftObject(NodeBuilder builder, Node node) {
        return toObject(builder, builder.set(node).getParamNode(0));
    }

    Object rightObject(NodeBuilder builder, Node node) {
        return toObject(builder, builder.set(node).getParamNode(1));
    }

    String leftString(NodeBuilder builder, Node node) {
        Object leftObj = leftObject(builder, node);
        if (leftObj != null)
            return (String) leftObj;
        return null;
    }

    String rightString(NodeBuilder builder, Node node) {
        Object leftObj = rightObject(builder, node);
        if (leftObj != null)
            return (String) leftObj;
        return null;
    }

    Double leftNumber(NodeBuilder builder, Node node) {
        Object leftObj = leftObject(builder, node);
        if (leftObj != null)
            return (Double) leftObj;
        return null;
    }

    Double rightNumber(NodeBuilder builder, Node node) {
        Object leftObj = rightObject(builder, node);
        if (leftObj != null)
            return (Double) leftObj;
        return null;
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
