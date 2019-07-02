package com.droid.djs.nodes.data;

import com.droid.djs.consts.NodeType;
import com.droid.djs.nodes.DataInputStream;
import com.droid.djs.nodes.Node;

import java.io.InputStream;

public class Data extends Node {

    public DataInputStream data; // TODO move to new node
    // TODO read external data in another thread
    public InputStream externalData;

    public Data(NodeType type) {
        super(type);
    }
}
