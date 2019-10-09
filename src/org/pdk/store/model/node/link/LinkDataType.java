package org.pdk.store.model.node.link;

public class LinkDataType {
    public static final byte NODE = 0;
    public static final byte BOOLEAN = NODE + 1;
    public static final byte NUMBER = BOOLEAN + 1;
    public static final byte STRING_START_LEANER_LENGTH = NUMBER + 1;
    public static final byte STRING_FINISH_LEANER_LENGTH = STRING_START_LEANER_LENGTH + Long.BYTES;
    public static final byte STRING_START_MULTI_LENGTH = STRING_FINISH_LEANER_LENGTH + 1;
    public static final byte FILE = Byte.MAX_VALUE;
}
