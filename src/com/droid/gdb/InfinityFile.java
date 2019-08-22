package com.droid.gdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

public class InfinityFile {

    private final static String INFINITY_FILE_PART_PREFIX = "part";
    private long partSize;
    public InfinityFileData fileData;
    private ActionThread mainThread;
    public DiskManager diskManager;
    protected String infinityFileName;

    public InfinityFile(String infinityFileDir, String infinityFileName) {
        this.infinityFileName = infinityFileName;
        diskManager = DiskManager.getInstance(infinityFileDir);
        this.mainThread = diskManager.actionThread;
        this.partSize = diskManager.partSize;

        fileData = diskManager.infinityFileCache.get(infinityFileName);
        if (fileData == null) {
            fileData = new InfinityFileData();
            Map<String, String> fileSettings = diskManager.properties.getSection(infinityFileName);
            if (fileSettings != null)
                for (int i = 0; fileSettings.containsKey(INFINITY_FILE_PART_PREFIX + i); i++) {
                    try {
                        String partFileName = fileSettings.get(INFINITY_FILE_PART_PREFIX + i);
                        RandomAccessFile partRandomAccessFile = new RandomAccessFile(partFileName, "rw");
                        fileData.files.add(partRandomAccessFile);
                        fileData.sumFilesSize += partRandomAccessFile.length();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            diskManager.infinityFileCache.put(infinityFileName, fileData);
        }

    }

    RandomAccessFile getPartFile(int index) {
        // TODO create file in action thread
        if (index == fileData.files.size()) {
            try {
                String partName = INFINITY_FILE_PART_PREFIX + index;
                String newFileName = infinityFileName + "." + partName;
                File partFile = new File(diskManager.dbDir.getAbsolutePath(), newFileName);
                diskManager.properties.put(infinityFileName, partName, partFile.getAbsolutePath());
                RandomAccessFile partRandomAccessFile = new RandomAccessFile(partFile, "rw");
                fileData.files.add(partRandomAccessFile);
                return partRandomAccessFile;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return fileData.files.get(index);
    }

    // TODO increase max file size from long to 2*long or bigger
    public byte[] read(long start, long length) {
        long end = start + length;
        if (end > fileData.sumFilesSize)
            return null;

        int startFileIndex = (int) (start / partSize);
        int endFileIndex = (int) (end / partSize);

        if (end != 0 && end % partSize == 0)
            endFileIndex -= 1;

        if (startFileIndex == endFileIndex) {
            RandomAccessFile readingFile = getPartFile(startFileIndex);
            int startInFile = (int) (start % partSize);
            return mainThread.read(readingFile, startInFile, (int) length);
        } else {
            RandomAccessFile firstFile = getPartFile(startFileIndex);
            RandomAccessFile secondFile = getPartFile(endFileIndex);
            int startInSecondFile = 0;
            int lengthInSecondFile = (int) (end % partSize);
            int startInFirstFile = (int) (start % partSize);
            int lengthInFirstFile = (int) (length - lengthInSecondFile);

            byte[] dataFromFirstFile = mainThread.read(firstFile, startInFirstFile, lengthInFirstFile);
            byte[] dataFromSecondFile = mainThread.read(secondFile, startInSecondFile, lengthInSecondFile);

            return Bytes.concat(dataFromFirstFile, dataFromSecondFile);
        }
    }

    public void write(long start, byte[] data) {
        long length = data.length;
        long end = start + length;
        if (start > fileData.sumFilesSize)
            return;

        int startFileIndex = (int) (start / partSize);
        int endFileIndex = (int) (end / partSize);

        RandomAccessFile firstWriteFile = getPartFile(startFileIndex);
        RandomAccessFile secondWriteFile = getPartFile(endFileIndex);

        if (start == fileData.sumFilesSize)
            fileData.sumFilesSize += data.length;

        if (end != 0 && end % partSize == 0)
            endFileIndex -= 1;

        if (startFileIndex == endFileIndex) {
            int startInFile = (int) (start - startFileIndex * partSize);
            mainThread.write(firstWriteFile, startInFile, data);
            // TODO archive thread
        } else {
            int startInSecondFile = 0;
            int lengthInSecondFile = (int) (end % partSize);
            int startInFirstFile = (int) (start % partSize);
            int lengthInFirstFile = (int) (length - lengthInSecondFile);

            byte[] dataToFirstFile = new byte[lengthInFirstFile];
            byte[] dataToSecondFile = new byte[lengthInSecondFile];

            System.arraycopy(data, 0, dataToFirstFile, 0, lengthInFirstFile);
            System.arraycopy(data, lengthInFirstFile, dataToSecondFile, 0, lengthInSecondFile);

            mainThread.write(firstWriteFile, startInFirstFile, dataToFirstFile);
            mainThread.write(secondWriteFile, startInSecondFile, dataToSecondFile);
            // TODO archive thread
        }
    }

    public long add(byte[] data) {
        long lastSumFileSize = fileData.sumFilesSize;
        write(lastSumFileSize, data);
        return lastSumFileSize;
    }

    public void close() throws IOException {
        for (RandomAccessFile file : fileData.files)
            file.close();
        diskManager.infinityFileCache.remove(infinityFileName);
    }
}
