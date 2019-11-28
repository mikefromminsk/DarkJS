package org.pdk.modules.prototypes;

import org.pdk.modules.Module;
import org.pdk.storage.NodeBuilder;
import org.pdk.storage.model.data.StringData;

public class StringPrototype extends Module {

    public StringPrototype(NodeBuilder builder) {
        super(builder);
    }

    @Override
    public String path() {
        return "String";
    }

    @Override
    public void methods() {
        func("reverse", (builder, ths) -> {
            StringData string = (StringData) builder.set(ths).getValue();
            byte[] array = string.bytes;
            for (int i = 0; i < array.length / 2; i++) {
                byte temp = array[i];
                array[i] = array[array.length - i - 1];
                array[array.length - i - 1] = temp;
            }
            // TODO add string hashing
            return string;
        });
    }
}
