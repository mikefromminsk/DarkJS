package com.droid.djs.runner.prototypes;

import com.droid.djs.nodes.Node;
import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.consts.NodeType;
import com.droid.djs.fs.Master;

public class DefaultPrototypes {

    // TODO change the system of prototypes. see DefaultPrototypes

    private static Node defaultPrototypes;// = builder.getObject(0L).getObject("defaultPrototypes"); // add version of prototypes

    private static NodeBuilder builder = new NodeBuilder();

    public static Node getInstance() {
        if (defaultPrototypes == null) {
            defaultPrototypes = initPrototypes();
            new NodeBuilder().set(Master.getInstance()).putObject("defaultPrototypes", defaultPrototypes);
        }
        return defaultPrototypes;
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

    static Node initPrototypes(){
        Node prototypes = builder.create().commit();
        prototype(prototypes, NodeType.STRING_NAME, initStringPrototype());
        prototype(prototypes, Console.PROTOTYPE_NAME, initConsolePrototype());
        prototype(prototypes, ThreadPrototype.PROTOTYPE_NAME, initThreadPrototype());
        return prototypes;
    }

    private static Node initConsolePrototype() {
        Node console = builder.create().commit();
        func(console, Console.LOG_NAME, Console.LOG);
        return console;
    }

    private static Node initStringPrototype() {
        Node string = builder.create().commit();
        func(string, Caller.STRING_TRIM_NAME, Caller.STRING_TRIM);
        func(string, Caller.STRING_REVERCE_NAME, Caller.STRING_REVERCE);
        return string;
    }

    private static Node initThreadPrototype() {
        Node thread = builder.create().commit();
        func(thread, ThreadPrototype.SLEEP_NAME, ThreadPrototype.SLEEP);
        return null;
    }
}
