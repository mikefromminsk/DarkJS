package org.pdk.store;

import org.simpledb.MetaCell;

import java.nio.ByteBuffer;

public class MetaCellWithType extends MetaCell {

    //private final static int META_CELL_SIZE = Byte.BYTES + 3 * Long.BYTES;
    public byte type;

    @Override
    public void parse(byte[] data) {
        ByteBuffer bytebuffer = ByteBuffer.wrap(data);
        type = bytebuffer.get();
        start = bytebuffer.getLong();
        length = bytebuffer.getLong();
    }

    @Override
    public byte[] build() {
        ByteBuffer bytebuffer = ByteBuffer.allocate(25/*META_CELL_SIZE*/);
        bytebuffer.put(type);
        bytebuffer.putLong(start);
        bytebuffer.putLong(length);
        return bytebuffer.array();
    }

    @Override
    public int getSize() {
        return 25/*META_CELL_SIZE*/;
    }
}