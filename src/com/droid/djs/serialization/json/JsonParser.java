package com.droid.djs.serialization.json;

import com.droid.djs.nodes.DataInputStream;
import com.google.gson.JsonElement;

import java.io.InputStreamReader;

public class JsonParser {
    // TODO delete com.google.gson and develop own parser
    private static com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();

    public static JsonElement parse(DataInputStream data) {
        return jsonParser.parse(new InputStreamReader(data));
    }
}
