package com.droid.djs.runner.utils;

import com.droid.Main;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.consts.NodeType;

public class Instance extends Utils {
    @Override
    public String name() {
        return "Instance";
    }

    @Override
    public void methods() {

        func("installer", (builder, node, ths) -> {
            String os = firstString(builder, node);
            Double clientVersion = getNumber(1, builder, node, 0d);
            if (clientVersion < Main.VERSION)
                return Files.getNode(node, "darkjs64.exe");
            else
                return null;
        }, par("os", NodeType.STRING), par("version", NodeType.NUMBER));

    }
}
