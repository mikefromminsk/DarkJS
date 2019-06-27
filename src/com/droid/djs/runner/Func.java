package com.droid.djs.runner;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.nodes.Node;

public interface Func {
    Node invoke(NodeBuilder builder, Node node, Node ths);
}
