package org.pdk.store.model.data;

public class StringData implements Data {
    public long stringId;
    public byte[] bytes;

    public StringData(long stringId) {
        this.stringId = stringId;
    }
}
