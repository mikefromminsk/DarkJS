package org.pdk.engine.store.nodes;

import org.pdk.engine.store.NodeType;

import java.io.InputStream;

public class Data extends Node {

    // TODO проблема что поток уже был считан но другой узел пытается его считать заново
    public DataInputStream data;
    // TODO read external data in another thread
    public InputStream externalData;

    public Data(NodeType type) {
        super(type);
    }
}
