package com.metabrain.djs;

import com.metabrain.djs.node.Node;
import com.metabrain.djs.node.NodeBuilder;
import com.metabrain.djs.node.NodeType;

public class DefaultPrototypes {

    private static Node defaultPrototypes;// = builder.getObject(0L).getObject("defaultPrototypes"); // add version of prototypes

    private static NodeBuilder builder = new NodeBuilder();

    public static Node getInstance() {
        if (defaultPrototypes == null) {
            defaultPrototypes = initPrototypes();
            builder.get(0L).putObject("defaultPrototypes", defaultPrototypes);
        }
        return defaultPrototypes;
    }

    static Node initPrototypes(){
        Node prototypes = builder.create().commit();
        prototype(prototypes, NodeType.STRING_NAME, initStringPrototype());
        return prototypes;
    }

    static void prototype(Node prototypes, String prototypeName, Node prototypeNode){
        Node prototypeNameNode = builder.create(NodeType.STRING).setData(prototypeName).commit();
        builder.set(prototypeNode).setTitle(prototypeNameNode).commit();
        builder.set(prototypes).addLocal(prototypeNode).commit();
    }

    static void func(Node prototypeParent, String functionName, int functionId){
        Node funcName = builder.create(NodeType.STRING).setData(functionName).commit();
        Node function = builder.create(NodeType.NATIVE_FUNCTION)
                .setTitle(funcName)
                .setFunctionId(functionId)
                .commit();
        builder.set(prototypeParent).addLocal(function);
    }

    private static Node initStringPrototype() {
        Node string = builder.create().commit();
        func(string, Caller.STRING_TRIM_NAME, Caller.STRING_TRIM);
        func(string, Caller.STRING_REVERCE_NAME, Caller.STRING_REVERCE);
        return string;
    }
}
