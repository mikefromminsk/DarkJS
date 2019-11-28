package org.pdk.converters;

import org.pdk.storage.NodeBuilder;
import org.pdk.storage.model.node.Node;

public abstract class ConverterBuilder {
    public NodeBuilder builder;

    public ConverterBuilder(NodeBuilder builder) {
        this.builder = builder;
    }

    public abstract Node build(Node module, Object parseResult);
}
