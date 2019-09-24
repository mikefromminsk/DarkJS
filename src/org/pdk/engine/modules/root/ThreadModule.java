package org.pdk.engine.modules.root;

import org.pdk.engine.store.NodeType;
import org.pdk.engine.modules.utils.Module;

public class ThreadModule extends Module {

    @Override
    public String name() {
        return "Thread";
    }

    @Override
    public void methods() {
        func("sleep", (builder, ths) -> {
            Double dalay = getNumber(builder, 0d, 0);
            if (dalay > 0) {
                try {
                    Thread.sleep((long) (double) dalay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }, par("delay", NodeType.NUMBER));
    }
}
