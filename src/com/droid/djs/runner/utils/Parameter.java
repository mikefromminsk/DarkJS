package com.droid.djs.runner.utils;

import com.droid.djs.consts.NodeType;

public class Parameter {
    String name;
    NodeType nodeType;

    public Parameter(String name, NodeType nodeType) {
        this.name = name;
        this.nodeType = nodeType;
    }
}
