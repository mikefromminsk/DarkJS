package org.pdk.modules.utils;

import org.pdk.store.model.data.Data;
import org.pdk.store.model.nodes.NodeBuilder;
import org.pdk.store.NodeType;
import org.pdk.files.Files;
import org.pdk.store.model.nodes.NativeNode;
import org.pdk.store.model.nodes.Node;
import org.pdk.instance.Instance;

import java.util.*;

abstract public class Module {

    public static String DEFAULT_PROTOTYPES_DIR = "Prototype/";

    public Module() {
        methods();
    }

    public void func(String name, Func func, Parameter... args) {
        Instance.get().functions.add(func);
        Instance.get().interfaces.add(new FuncInterface((name().endsWith("/") ? name() : name() + "/"), name, Arrays.asList(args)));
    }

    public static void saveInterfaces() {
        NodeBuilder builder = new NodeBuilder();
        List<FuncInterface> interfaces = Instance.get().interfaces;
        for (int i = 0; i < interfaces.size(); i++) {
            FuncInterface funcInterface = interfaces.get(i);
            String functionName = funcInterface.path + funcInterface.name;
            NativeNode function = (NativeNode) Files.getNodeFromRoot(functionName, NodeType.NATIVE_FUNCTION);
            builder.set(function).setFunctionIndex(i);
            for (Parameter parameter : funcInterface.parameters) {
                Node param = parNode(builder, parameter);
                builder.set(function).addParam(param).commit();
            }
        }
    }

    public static FuncInterface getFunctionInterface(int index) {
        return Instance.get().interfaces.get(index);
    }

    private static Node parNode(NodeBuilder builder, Parameter parameter) {
        Node title = builder.createString(parameter.name);
        Node defValue;
        if (parameter.nodeType == NodeType.NUMBER)
            defValue = builder.createNumber(0D);
        else if (parameter.nodeType == NodeType.STRING)
            defValue = builder.createString("string");
        else
            defValue = builder.create(parameter.nodeType).commit();
        return builder.create().setTitle(title).setValue(defValue).commit();
    }

    public Parameter par(String name, NodeType nodeType) {
        return new Parameter(name, nodeType);
    }

    protected Object getObject(NodeBuilder builder, int index) {
        Node prev = builder.getNode();
        builder.set(builder.getParamNode(index));
        Object obj = toObject(builder);
        builder.set(prev);
        return obj;
    }

    protected String getString(NodeBuilder builder, int index) {
        Object leftObj = getObject(builder, index);
        if (leftObj != null)
            return (String) leftObj;
        return null;
    }

    String getStringRequired(NodeBuilder builder, int index) {
        String result = getString(builder, index);
        if (result != null)
            return result;
        throw new IllegalArgumentException();
    }

    protected Double getNumber(NodeBuilder builder, Double def, int index) {
        Object leftObj = getObject(builder, index);
        if (leftObj != null)
            return (Double) leftObj;
        return def;
    }

    protected Object toObject(NodeBuilder builder) {
        Node node = builder.getNode();
        if (node != null)
            node = builder.getValueOrSelf();
        Object obj = null;
        if (node instanceof Data)
            obj = ((Data) node).data.getObject();
        return obj;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    protected Node getNode(NodeBuilder builder, int i) {
        return builder.getParamNode(i);
    }

    public abstract String name();

    public abstract void methods();
}
