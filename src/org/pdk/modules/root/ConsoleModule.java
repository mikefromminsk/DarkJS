package org.pdk.modules.root;

import org.pdk.store.consts.NodeType;
import org.pdk.modules.Module;

public class ConsoleModule extends Module {
    @Override
    public String name() {
        return "ConsoleModule";
    }

    @Override
    public void methods() {
        func("log", (builder, ths) -> {
            String message = getString(builder, 0);
            System.out.println(message);
            return builder.createBool(true);
        }, par("message", NodeType.STRING));
    }
}
