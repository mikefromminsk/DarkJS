package org.pdk.store.model.data;

import org.pdk.store.Storage;

public class StringData implements Data {

    private Storage storage;
    private byte[] bytes;

    public StringData(Storage storage) {
        this.storage = storage;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
