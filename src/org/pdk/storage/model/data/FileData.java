package org.pdk.storage.model.data;


import org.pdk.storage.Storage;

import java.io.*;
import java.util.Random;

public class FileData implements Data {
    private final static Random random = new Random();
    private Storage storage;
    public Integer fileId;
    public File file;

    public FileData(Storage storage, InputStream stream) throws IOException {
        this.storage = storage;
        fileId = random.nextInt();
        getFile().getParentFile().mkdirs();
        OutputStream outStream = new FileOutputStream(getFile(), false);
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1)
            outStream.write(buffer, 0, bytesRead);
        stream.close();
        outStream.close();
    }

    public FileData(Storage storage, Integer fileId) {
        this.storage = storage;
        this.fileId = fileId;
    }

    public File getFile() {
        if (file == null)
            file = new File(storage.storeDir + "tmp/" + fileId + ".data");
        return file;
    }
}
