package org.pdk.store.model.data.string;

public class MediumStringData extends StringData {

    public long stringId;
    public byte[] bytes;

    public MediumStringData(long stringId) {
        this.stringId = stringId;
    }

    public MediumStringData(byte[] bytes) {
        this.bytes = bytes;
    }
}
