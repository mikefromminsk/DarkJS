package org.pdk.funcitons;

import org.pdk.storage.NodeBuilder;
import org.pdk.storage.model.DataOrNode;
import org.pdk.storage.model.node.Node;

public abstract class Function {

    public String moduleName;
    public String functionName;
    public String[] arguments;

    public Function(String moduleName, String functionName, String... arguments) {
        this.moduleName = moduleName;
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public abstract DataOrNode invoke(NodeBuilder builder, Node ths);

    public String path(){
        return  moduleName + "/" + functionName;
    }
}
