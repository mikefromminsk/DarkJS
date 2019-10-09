package org.pdk.files.converters;

import org.pdk.files.converters.js.JsBuilder;
import org.pdk.files.converters.js.JsParser;
import org.pdk.store.NodeBuilder;
import org.pdk.store.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConverterManager {
    private List<Converter> allConverters = new ArrayList<>();
    public java.util.Map<String, Converter> fileConverters = new HashMap<>();

    public ConverterManager(Storage storage) {
        allConverters.add(new Converter(new String[]{"js"}, new JsParser(new NodeBuilder(storage)), new JsBuilder(new NodeBuilder(storage))));
    }
}
