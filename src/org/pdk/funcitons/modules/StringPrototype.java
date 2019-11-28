package org.pdk.funcitons.modules;

import org.pdk.funcitons.Function;
import org.pdk.funcitons.FunctionManager;
import org.pdk.storage.NodeBuilder;
import org.pdk.storage.model.DataOrNode;
import org.pdk.storage.model.data.StringData;
import org.pdk.storage.model.node.Node;

public class StringPrototype {

    public StringPrototype(FunctionManager functions) {
        functions.reg(new Function("String", "reverse") {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                StringData string = (StringData) builder.set(ths).getValue();
                byte[] array = string.bytes;
                for (int i = 0; i < array.length / 2; i++) {
                    byte temp = array[i];
                    array[i] = array[array.length - i - 1];
                    array[array.length - i - 1] = temp;
                }
                // TODO add string hashing
                return string;
            }
        });
    }
}
