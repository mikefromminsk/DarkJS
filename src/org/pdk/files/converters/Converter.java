package org.pdk.files.converters;

import org.pdk.store.NodeBuilder;

public abstract class Converter {
    public NodeBuilder builder;

    public Converter(NodeBuilder builder) {
        this.builder = builder;
    }

    public abstract String[] supportExceptions();

    public abstract ConverterParser parser();

    public abstract ConverterBuilder creator();
    //public abstract ConverterBuilder serialize();
}
