package org.pdk.modules.utils;

import org.pdk.store.model.node.NodeBuilder;
import org.pdk.store.model.node.Node;

public interface Func {
    Node invoke(NodeBuilder builder, Node ths);
}
