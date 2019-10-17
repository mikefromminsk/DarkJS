package org.pdk.converters;

import org.pdk.converters.js.JsBuilder;
import org.pdk.converters.js.JsParser;
import org.pdk.store.NodeBuilder;
import org.pdk.store.Storage;
import java.util.HashMap;

public class ConverterManager {
    public java.util.Map<String, Converter> fileConverters = new HashMap<>();

    public ConverterManager(Storage storage) {
        addConverter(new Converter(
                new String[]{"js"},
                new JsParser(),
                new JsBuilder(new NodeBuilder(storage))));
    }

    void addConverter(Converter converter){
        for (String extension: converter.extensions)
            fileConverters.put(extension, converter);
    }
}
