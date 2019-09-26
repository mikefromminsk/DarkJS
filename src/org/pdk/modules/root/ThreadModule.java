package org.pdk.modules.root;

import org.pdk.store.consts.NodeType;
import org.pdk.modules.Module;

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
