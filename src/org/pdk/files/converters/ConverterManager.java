package org.pdk.files.converters;

import org.pdk.files.converters.js.JsConverter;
import org.pdk.store.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConverterManager {
    private List<Converter> converters = new ArrayList<>();
    private java.util.Map<String, Converter> wconverters = new HashMap<>();

    public ConverterManager(Storage storage) {
        converters.add(new JsConverter(storage));

    }
}
