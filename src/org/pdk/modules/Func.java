package org.pdk.modules;

import org.pdk.storage.NodeBuilder;
import org.pdk.storage.model.DataOrNode;
import org.pdk.storage.model.node.Node;

public interface Func {
    DataOrNode invoke(NodeBuilder builder, Node ths);
}
