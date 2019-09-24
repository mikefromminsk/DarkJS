package org.pdk.modules.utils;

import org.pdk.store.nodes.NodeBuilder;
import org.pdk.store.nodes.Node;

public interface Func {
    Node invoke(NodeBuilder builder, Node ths);
}
