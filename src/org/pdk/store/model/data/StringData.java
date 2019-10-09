package org.pdk.store.model.data;

import org.pdk.store.Storage;

public class StringData implements Data {

    protected Storage storage;
    private byte[] bytes;

    protected StringData(Storage storage) {
        this.storage = storage;
    }

    public StringData(Storage storage, byte[] bytes) {
        this(storage);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
