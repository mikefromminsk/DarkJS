package com.droid.djs.nodes;

import com.droid.djs.NodeStorage;

public class DataNode extends Node {
    public DataInputStream data; // TODO move to new node

    public DataNode(byte type) {
        super(type);
    }

    public void init(NodeStorage.NodeMetaCell metaCell){
        data = new DataInputStream(metaCell);
    }
}
