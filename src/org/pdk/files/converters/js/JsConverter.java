package org.pdk.files.converters.js;

import org.pdk.files.converters.Converter;
import org.pdk.files.converters.ConverterBuilder;
import org.pdk.files.converters.ConverterParser;
import org.pdk.store.NodeBuilder;
import org.pdk.store.Storage;

public class JsConverter extends Converter {
    public JsConverter(Storage storage) {
        super(storage);
    }

    @Override
    public String[] extensions() {
        return new String[]{"js", "min.js"};
    }

    @Override
    public ConverterParser parser() {
        return new JsParser(new NodeBuilder(storage));
    }

    @Override
    public ConverterBuilder builder() {
        return new JsBuilder(new NodeBuilder(storage));
    }
}
