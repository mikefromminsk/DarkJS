package org.pdk.store.model.data;


import java.io.FileInputStream;

public class FileData implements Data {
    public Long fileId;
    byte[] byffer;
    FileInputStream in;

    public FileData(Long fileId) {
        this.fileId = fileId;
    }
}
