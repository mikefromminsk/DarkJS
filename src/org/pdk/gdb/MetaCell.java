package org.pdk.gdb;

public class MetaCell implements InfinityConstArrayCell {

    public long start;
    public long length;

    @Override
    public void parse(byte[] data) {
        long[] metaOfIndex = Bytes.toLongArray(data);
        start = metaOfIndex[0];
        length = metaOfIndex[1];
    }

    @Override
    public byte[] build() {
        long[] data = new long[3];
        data[0] = start;
        data[1] = length;
        return Bytes.fromLongArray(data);
    }

    @Override
    public int getSize() {
        // TODO DANGER Actual value is (2 * Long.Byte)
        return 24; // 3 * Long.BYTE
    }
}