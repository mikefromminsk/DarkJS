package org.pdk.modules.utils;

import org.pdk.store.model.nodes.NodeBuilder;
import org.pdk.store.model.nodes.Node;

public interface Func {
    Node invoke(NodeBuilder builder, Node ths);
}
