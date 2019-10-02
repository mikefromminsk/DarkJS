package org.pdk.files.converters.utils;

public abstract class Converter {
    public abstract String[] supportExceptions();
    public abstract ConverterParser parser();
    public abstract ConverterBuilder builder();
    //public abstract ConverterBuilder serialize();
}
