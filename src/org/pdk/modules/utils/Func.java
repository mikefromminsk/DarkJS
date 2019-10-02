package org.pdk.modules.utils;

import org.pdk.store.NodeBuilder;
import org.pdk.store.model.DataOrNode;
import org.pdk.store.model.node.Node;

public interface Func {
    DataOrNode invoke(NodeBuilder builder, Node ths);
}
