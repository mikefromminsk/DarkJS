package com.droid.djs.serialization.json;

import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.consts.NodeType;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Map;

public class JsonBuilder {

    private static NodeBuilder builder = new NodeBuilder();

    public static void build(Node file, JsonElement je) {
        Node obj = build(je);
        Files.replace(file, obj);
    }

    public static Node build(JsonElement je) {
        /*if (je.isJsonNull())
            return "null";*/

        if (je.isJsonPrimitive()) {
            JsonPrimitive primitive = je.getAsJsonPrimitive();
            if (primitive.isBoolean())
                return builder.create(NodeType.BOOLEAN).setData(primitive.getAsBoolean()).commit();
            if (primitive.isString())
                return builder.create(NodeType.STRING).setData(primitive.getAsString()).commit();
            if (primitive.isNumber())
                return builder.create(NodeType.NUMBER).setData(primitive.getAsDouble()).commit();
        }

        if (je.isJsonArray()) {
            Node arr = builder.create(NodeType.ARRAY).commit();
            for (JsonElement item : je.getAsJsonArray()) {
                Node node = build(item);
                builder.set(arr).addCell(node).commit();
            }
            return arr;
        }

        if (je.isJsonObject()) {
            Node obj = builder.create().commit();
            for (Map.Entry<String, JsonElement> e : je.getAsJsonObject().entrySet()) {
                Node value = build(e.getValue());
                Node title = builder.create(NodeType.STRING).setData(e.getKey()).commit();
                Node var = builder.create().setTitle(title).setValue(value).commit();
                builder.set(obj).addLocal(var).commit();
            }
            return obj;
        }
        return null;
    }
}
