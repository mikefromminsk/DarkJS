package com.droid.djs.runner.prototypes;

import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.consts.NodeType;
import com.droid.djs.runner.utils.Utils;

public class Prototypes {

    private static Node defaultPrototypesDir;

    public static Node getInstance() {
        if (defaultPrototypesDir == null) {
            defaultPrototypesDir = Files.getNode(Utils.DEFAULT_PROTOTYPES_DIR, null);
            if (defaultPrototypesDir == null){
                new StringPrototype().methods();
                // add into this pos other prototypes initialization
                defaultPrototypesDir = Files.getNode(Utils.DEFAULT_PROTOTYPES_DIR);
            }
        }
        return defaultPrototypesDir;
    }

    // TODO delete
    public static Node get(Byte nodeType) {
        return Files.getNode(getInstance(), NodeType.toString(nodeType), null);
    }
}
