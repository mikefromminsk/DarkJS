package org.pdk.store.model.data;

public class StringData implements Data {

    public byte[] bytes;

    public StringData(byte[] bytes) {
        this.bytes = bytes;
    }
}
