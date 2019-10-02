package org.pdk.files.converters.utils;

import org.pdk.store.NodeBuilder;

public abstract class ConverterParser {
    private NodeBuilder builder;

    public ConverterParser(NodeBuilder builder) {
        this.builder = builder;
    }

    abstract Object parse();
}
