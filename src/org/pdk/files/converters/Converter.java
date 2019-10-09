package org.pdk.files.converters;

public class Converter {
    private String[] extensions;
    private ConverterParser parser;
    private ConverterBuilder builder;

    public Converter(String[] extensions, ConverterParser parser, ConverterBuilder builder) {
        this.extensions = extensions;
        this.parser = parser;
        this.builder = builder;
    }

    public ConverterParser getParser() {
        return parser;
    }

    public ConverterBuilder getBuilder() {
        return builder;
    }
}
