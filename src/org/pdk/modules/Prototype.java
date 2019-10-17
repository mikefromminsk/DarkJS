package org.pdk.modules;

import org.pdk.store.NodeBuilder;

public abstract class Prototype extends Module {

    public static String DEFAULT_PROTOTYPES_DIR = "Prototype/";

    public Prototype(NodeBuilder builder) {
        super(builder);
    }

    public abstract String name();

    @Override
    public String path() {
        return DEFAULT_PROTOTYPES_DIR + name();
    }
}
