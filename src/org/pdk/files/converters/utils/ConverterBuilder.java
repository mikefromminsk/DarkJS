package org.pdk.files.converters.utils;

import org.pdk.store.NodeBuilder;

public abstract class ConverterBuilder {
    NodeBuilder builder;

    public ConverterBuilder(NodeBuilder builder) {
        this.builder = builder;
    }
}
