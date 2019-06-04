package com.droid.net.ftp;

import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;

public class Master {

    private static Node instance = null;
    public static Node getInstance() {
        if (instance == null){
            instance = new NodeBuilder().get(0L).getLocalNode(0);
        }
        return instance;
    }
}
