package org.pdk.files.converters;

import org.pdk.store.Storage;

public abstract class Converter {
    protected Storage storage;

    public Converter(Storage storage) {
        this.storage = storage;
    }

    public abstract String[] extensions();

    public abstract ConverterParser parser();

    public abstract ConverterBuilder builder();
    //public abstract ConverterBuilder serialize();
}
