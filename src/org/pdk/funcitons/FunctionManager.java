package org.pdk.funcitons;

import org.pdk.funcitons.modules.Math;
import org.pdk.funcitons.modules.StringPrototype;
import org.pdk.storage.NodeBuilder;
import org.pdk.storage.model.node.Node;

import java.util.HashMap;
import java.util.Map;

public class FunctionManager {

    private NodeBuilder builder;
    public StringPrototype stringPrototype;
    // TODO test map speed with String and ByteArray
    public static Map<String, Function> functions = new HashMap<>();

    public FunctionManager(NodeBuilder builder) {
        this.builder = builder;
        // init standard modules
        new Math(this);
        stringPrototype = new StringPrototype(this);
    }

    public void reg(Function function) {
        Node moduleNode = builder.getNodeFromRoot(function.moduleName);
        Node funcNode = builder.getNode(moduleNode, function.functionName);
        funcNode.function = function;
        for (String arg: function.arguments) {
            Node argNode = builder.create().setTitle(arg).commit();
            builder.set(funcNode).addParam(argNode).commit();
        }
        functions.put(function.path(), function);
    }
}
