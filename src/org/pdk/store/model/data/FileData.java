package org.pdk.store.model.data;


import org.pdk.store.Storage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class FileData implements Data {
    private final Random random = new Random();
    public Integer fileId;
    byte[] byffer;
    FileInputStream in;

    public FileData(Storage storage, InputStream stream) throws IOException {
        fileId = random.nextInt();
        long length = Files.copy(stream, Paths.get(storage.storeDir, "" + fileId, ".data"));
    }
}
