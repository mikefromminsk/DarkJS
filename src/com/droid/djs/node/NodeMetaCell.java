package com.droid.djs.node;

import com.droid.gdb.MetaCell;

import java.nio.ByteBuffer;

public class NodeMetaCell extends MetaCell {

    private final static int META_CELL_SIZE = Byte.BYTES + 3 * Long.BYTES;
    public byte type;

    @Override
    public void parse(byte[] data) {
        ByteBuffer bytebuffer = ByteBuffer.wrap(data);
        type = bytebuffer.get();
        start = bytebuffer.getLong();
        length =  bytebuffer.getLong();
        accessKey = bytebuffer.getLong();
    }

    @Override
    public byte[] build() {
        ByteBuffer bytebuffer = ByteBuffer.allocate(META_CELL_SIZE);
        bytebuffer.put(type);
        bytebuffer.putLong(start);
        bytebuffer.putLong(length);
        bytebuffer.putLong(accessKey);
        return bytebuffer.array();
    }

    @Override
    public int getSize() {
        return META_CELL_SIZE;
    }
}
