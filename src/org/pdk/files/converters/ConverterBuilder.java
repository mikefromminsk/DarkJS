package org.pdk.files.converters;

import org.pdk.store.NodeBuilder;
import org.pdk.store.model.node.Node;

public abstract class ConverterBuilder {
    public NodeBuilder builder;

    public ConverterBuilder(NodeBuilder builder) {
        this.builder = builder;
    }

    public abstract Node build(Node module, Object parseResult);
}
