package org.pdk.modules;

import org.pdk.modules.prototypes.StringPrototype;
import org.pdk.modules.root.*;
import org.pdk.store.NodeBuilder;

public class ModuleManager {

    public StringPrototype stringPrototype;

    public ModuleManager(NodeBuilder builder) {
        stringPrototype = new StringPrototype(builder);
        // load modules
        new MathModule(builder);
        // registration in root
    }

}
