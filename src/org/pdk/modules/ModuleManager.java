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

    public List<Func> initInterfaces(NodeBuilder builder) {
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
        for (int i = 0; i < interfaces.size(); i++) {
            FuncInterface funcInterface = interfaces.get(i);
            String funcPath = funcInterface.path + funcInterface.name;
            Node function = Files.getNodeFromRoot(builder, funcPath, true);
            builder.set(function).setFunc(i);
            for (String paramName: funcInterface.parameters) {
                Node param = builder.create().setTitle(paramName).commit();
                builder.set(function).addParam(param).commit();
            }
        }
    }
}
