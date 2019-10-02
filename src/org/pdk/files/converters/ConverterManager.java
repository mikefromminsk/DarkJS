package org.pdk.files.converters;

import org.pdk.files.converters.js.JsConverter;
import org.pdk.files.converters.utils.Converter;

import java.util.ArrayList;
import java.util.List;

public class ConverterManager {
    List<Converter> converters = new ArrayList<>();

    void initConverters(){
        if (converters.size() == 0){
            converters.add(new JsConverter());
        }
    }
}
