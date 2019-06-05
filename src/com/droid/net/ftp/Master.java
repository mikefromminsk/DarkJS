package com.droid.net.ftp;

import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;

public class Master {

    private static Node instance = null;
    public static Node getInstance() {
        if (instance == null){
            NodeBuilder builder = new NodeBuilder().get(0L);
            instance = builder.getLocalNode(0);
            if (instance == null){
                Node master = builder.create().commit();
                builder.get(0L).addLocal(master).commit();
                instance = master;
            }
        }
        return instance;
    }
}
