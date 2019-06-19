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
            long linkData = (dataLink - linkType) / 256;
            restore(linkType, linkData);
        }
    }

    abstract void restore(byte linkType, long linkData);

    public interface NodeLinkListener {
        void get(byte linkType, Object link, boolean singleValue);
    }

    abstract void listLinks(Node.NodeLinkListener linkListener);
}
