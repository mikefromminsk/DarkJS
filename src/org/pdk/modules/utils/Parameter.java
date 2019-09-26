package org.pdk.modules.utils;

import org.pdk.store.consts.NodeType;

public class Parameter {
    String name;
    NodeType nodeType;

    public Parameter(String name, NodeType nodeType) {
        this.name = name;
        this.nodeType = nodeType;
    }
}
