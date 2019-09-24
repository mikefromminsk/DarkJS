package org.pdk.engine.modules.utils;

import org.pdk.engine.consts.NodeType;

public class Parameter {
    String name;
    NodeType nodeType;

    public Parameter(String name, NodeType nodeType) {
        this.name = name;
        this.nodeType = nodeType;
    }
}
