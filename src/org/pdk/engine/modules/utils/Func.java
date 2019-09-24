package org.pdk.engine.modules.utils;

import org.pdk.engine.store.nodes.NodeBuilder;
import org.pdk.engine.store.nodes.Node;

public interface Func {
    Node invoke(NodeBuilder builder, Node ths);
}
