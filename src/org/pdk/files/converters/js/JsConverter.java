package org.pdk.files.converters.js;

import org.pdk.files.converters.utils.Converter;
import org.pdk.files.converters.utils.ConverterBuilder;
import org.pdk.files.converters.utils.ConverterParser;
import org.pdk.store.NodeBuilder;

public class JsConverter extends Converter {
    public JsConverter(NodeBuilder builder) {
        super(builder);
    }

    @Override
    public String[] supportExceptions() {
        return new String[]{"js", "min.js"};
    }

    @Override
    public ConverterParser parser() {
        return new JsParser(builder);
    }

    @Override
    public ConverterBuilder creator() {
        return new JsBuilder(builder);
    }
}
