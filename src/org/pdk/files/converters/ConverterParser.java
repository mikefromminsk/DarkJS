package org.pdk.files.converters;

import org.pdk.store.NodeBuilder;

public abstract class ConverterParser {
    private NodeBuilder builder;

    public ConverterParser(NodeBuilder builder) {
        this.builder = builder;
    }

    protected abstract Object parse();
}
