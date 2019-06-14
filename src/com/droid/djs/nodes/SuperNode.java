package com.droid.djs.nodes;

import com.droid.gdb.Bytes;
import com.droid.gdb.InfinityStringArrayCell;

public abstract class SuperNode implements InfinityStringArrayCell {

    @Override
    public byte[] build() {
        return new byte[0];
    }

    @Override
    public void parse(byte[] data) {
        long[] links = Bytes.toLongArray(data);
        for (long dataLink : links) {
            byte linkType = (byte) (dataLink % 256);
            long linkId = (dataLink - linkType) / 256;
            restore(linkType, linkId);
        }
    }

    abstract void restore(byte linkType, long linkId);

    public interface NodeLinkListener {
        void get(byte linkType, Object link, boolean singleValue);
    }

    abstract void listLinks(Node.NodeLinkListener linkListener);
}
