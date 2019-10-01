package org.pdk.store.model.node.meta;

import org.simpledb.MetaCell;

import java.nio.ByteBuffer;

public class NodeMeta extends MetaCell {

    private final static int META_CELL_SIZE = Byte.BYTES + (2 * Long.BYTES);

    public NodeType type;

    @Override
    public void parse(byte[] data) {
        ByteBuffer bytebuffer = ByteBuffer.wrap(data);
        type = NodeType.values()[bytebuffer.get()];
        start = bytebuffer.getLong();
        length = bytebuffer.getLong();
    }

    @Override
    public byte[] build() {
        ByteBuffer bytebuffer = ByteBuffer.allocate(META_CELL_SIZE);
        bytebuffer.put((byte) type.ordinal());
        bytebuffer.putLong(start);
        bytebuffer.putLong(length);
        return bytebuffer.array();
    }

    @Override
    public int getSize() {
        return META_CELL_SIZE;
    }
}