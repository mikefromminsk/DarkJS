package org.pdk.store.model.data;


import org.pdk.store.Storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class FileData extends StringData {
    private final Random random = new Random();
    public Integer fileId;
    public File file;

    public FileData(Storage storage, InputStream stream) throws IOException {
        super(storage);
        fileId = random.nextInt();
        // TODO
        long length = Files.copy(stream, Paths.get(storage.storeDir + fileId + ".data"));
    }

    public FileData(Storage storage, Integer fileId) {
        super(storage);
        this.fileId = fileId;
    }

    public Integer getFileId() {
        return fileId;
    }

    public File getFile(){
        if (file != null)
            file = new File(storage.storeDir + fileId + ".data");
        return file;
    }
}
