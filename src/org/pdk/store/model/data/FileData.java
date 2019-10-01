package org.pdk.store.model.data;

import java.io.*;

public class FileData extends FileInputStream implements Data {

    public Long id;

    public FileData(Long id, File file) throws FileNotFoundException {
        super(file);
        this.id = id;
    }

}
