package org.pdk.files.convertors.json;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public class JsonParser {
    // TODO delete com.google.gson and develop own parser
    private static com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();

    public static JsonElement parse(String data) {
        JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
        reader.setLenient(true);
        return jsonParser.parse(reader);
    }
}
