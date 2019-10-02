package org.pdk.store.model.node.link;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Link {
    public static final int SIZE = 10;
    public LinkType linkType;
    public LinkDataType linkDataType;
    public ByteBuffer linkData = ByteBuffer.allocate(8);

    public void parse(byte[] data) {
        linkType = LinkType.values()[data[0/* 0 - linkTypeOffset*/]];
        linkDataType = LinkDataType.values()[data[1/*linkDataTypeOffset*/]];
        linkData = linkData.put(Arrays.copyOfRange(data, 2/*linkDataOffset*/, 9/*linkDataFinishIndex*/));
    }

    public byte[] build() {
        ByteBuffer bb = ByteBuffer.allocate(SIZE);
        bb.put((byte) linkType.ordinal());
        bb.put((byte) linkDataType.ordinal());
        bb.put(linkData);
        return bb.array();
    }
}
