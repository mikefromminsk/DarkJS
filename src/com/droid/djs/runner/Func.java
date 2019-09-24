package com.droid.djs.runner;

import com.droid.djs.store_models.nodes.NodeBuilder;
import com.droid.djs.store_models.nodes.Node;

public interface Func {
    Node invoke(NodeBuilder builder, Node ths);
}
