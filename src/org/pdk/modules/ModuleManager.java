package org.pdk.modules;

import org.pdk.files.Files;
import org.pdk.modules.prototypes.StringPrototype;
import org.pdk.modules.root.*;
import org.pdk.store.NodeBuilder;
import org.pdk.store.model.node.Node;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    public static List<Func> functions = new ArrayList<>();
    public static List<FuncInterface> interfaces = new ArrayList<>();

    public static List<Func> initInterfaces(NodeBuilder builder) {
        if (functions.size() == 0) {
            // load prototypes
            new StringPrototype();
            // load modules
            new MathModule();
            // registration in root
            saveInterfaces(builder);
        }
        return functions;
    }

    public static void saveInterfaces(NodeBuilder builder) {
        for (FuncInterface funcInterface: interfaces) {
            Node funcNode = Files.getNodeFromRoot(builder, funcInterface.path + funcInterface.name);
            funcNode.func = funcInterface.func;
            for (String paramName: funcInterface.parameters) {
                Node param = builder.create().setTitle(paramName).commit();
                builder.set(funcNode).addParam(param).commit();
            }
        }
    }
}
