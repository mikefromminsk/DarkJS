package com.droid.djs.serialization.json;

import com.droid.djs.store_models.nodes.Data;
import com.droid.djs.store_models.nodes.Node;
import com.droid.djs.store_models.nodes.NodeBuilder;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonSerializer {

    private static Gson json = new GsonBuilder().setPrettyPrinting().create();

    public static String serialize(NodeBuilder builder) {
        Object obj = toObject(builder);
        return json.toJson(obj);
    }

    public static Object toObject(NodeBuilder builder) {
        if (builder.isArray()) {
            List<Object> arr = new ArrayList<>();
            for (Node cell : builder.getCells())
                if (!builder.set(cell).isFunction())
                    arr.add(toObject(builder));
            return arr;
        } else if (builder.getLocalCount() > 0) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Node local : builder.getLocalNodes())
                if (!builder.set(local).isFunction()) {
                    String field = builder.getTitleString();
                    map.put(field, toObject(builder));
                }
            return map;
        } else if (builder.getValueNode() != null) {
            Node value = builder.getValueNode();
            if (value instanceof Data)
                return ((Data) value).data.getObject();
        }
        return null;
    }
}
