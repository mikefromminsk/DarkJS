package org.pdk.modules;

import org.pdk.store.model.node.NodeBuilder;
import org.pdk.store.model.node.Node;

public interface Func {
    Node invoke(NodeBuilder builder, Node ths) throws Exception;
}
