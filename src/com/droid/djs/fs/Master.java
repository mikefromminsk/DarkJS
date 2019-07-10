package com.droid.djs.fs;

import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.nodes.Node;
import com.droid.djs.serialization.node.NodeBuilder;

public class Master {

    private static Node instance = null;
    public static Node getInstance() {
        if (instance == null){
            NodeBuilder builder = new NodeBuilder().get(0L);
            instance = builder.getLocalNode(0);
            if (instance == null){
                Node master = builder.create(NodeType.THREAD).commit();
                builder.get(0L).addLocal(master).commit();
                instance = master;
            }
        }
        return instance;
    }

    // TODO change to change instance
    public static void removeInstance(){
        instance = null;
    }
}
