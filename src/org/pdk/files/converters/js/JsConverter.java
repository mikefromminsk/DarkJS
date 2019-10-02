package org.pdk.files.converters.js;

import org.pdk.files.converters.utils.Converter;
import org.pdk.files.converters.utils.ConverterBuilder;
import org.pdk.files.converters.utils.ConverterParser;

public class JsConverter extends Converter {
    @Override
    public String[] supportExceptions() {
        return new String[]{"js", "min.js"};
    }

    @Override
    public ConverterParser parser() {
        return new JsParser();
    }

    @Override
    public ConverterBuilder builder() {
        return new JsBuilder();
    }
}
