package com.droid.djs.nodes;

import com.droid.djs.consts.NodeType;

import java.io.InputStream;

public class Data extends Node {

    public DataInputStream data; // TODO move to new node
    // TODO read external data in another thread
    public InputStream externalData;

    public Data(NodeType type) {
        super(type);
    }
}
