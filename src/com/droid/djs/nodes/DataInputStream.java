package com.droid.djs.nodes;

import com.droid.djs.DataStorage;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.gdb.Bytes;
import com.droid.gdb.DiskManager;
import com.droid.instance.Instance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class DataInputStream extends InputStream {

    // TODO setString buffer size > MAX_STORAGE_DATA_IN_DB
    private static final int BUFFER_SIZE = DataStorage.MAX_STORAGE_DATA_IN_DB;
    private NodeType type;
    public long start;
    public long length;
    private long currentPosition;
    private DataStorage dataStorage = Instance.get().getDataStorage();
    private FileInputStream fileReader;

    public DataInputStream( NodeType type, long start, long length) {
        this.type = type;
        this.start = start;
        this.length = length;
        currentPosition = 0;
    }

    public boolean hasNext() {
        boolean nextExist = currentPosition < length;
        if (!nextExist){
            currentPosition = 0;
            if (fileReader != null){
                try {
                    fileReader.close();
                    fileReader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return nextExist;
    }

    private byte[] readFromDb() {
        byte[] data = dataStorage.getData(start, currentPosition, (int) Math.min(BUFFER_SIZE, length));
        currentPosition += data.length;
        return data;
    }

    private byte[] readFromFs() {
        try {
            if (fileReader == null)
                fileReader = new FileInputStream(DiskManager.getInstance(Instance.get().storeDir).getFileById(start));
            byte[] buf = new byte[BUFFER_SIZE];
            int readiedChars = fileReader.read(buf);
            if ((readiedChars) > 0) {
                if (readiedChars < BUFFER_SIZE)
                    buf = Arrays.copyOf(buf, readiedChars); // removeSector zero bytes
                currentPosition += readiedChars;
                fileReader.close();
                return buf;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String readString() {
        return new String(readBytes());
    }

    public byte[] readBytes() {
        byte[] allstring = new byte[0];
        while (hasNext()) {
            byte[] buffer;
            if (length < DataStorage.MAX_STORAGE_DATA_IN_DB)
                buffer = readFromDb();
            else
                buffer = readFromFs();
            if (buffer != null)
                allstring = Bytes.concat(allstring, buffer);
            if (buffer == null) // TODO DANGER INFINITY LOOP
                throw new NullPointerException();
        }
        return allstring;
    }

    public Object getObject() {
        String string = readString();
        switch (type) {
            case BOOL:
                return Boolean.valueOf(string);
            case NUMBER:
                return Double.valueOf(string);
            default:
                return string;
        }
    }

    @Override
    public int read() {
        // TODO !!!!
        return 0;
    }

    @Override
    public int read(byte[] b) {
        // TODO !!!! rewrite
        // Multithreading read
        if (currentPosition == length){
            currentPosition = 0;
            return -1;
        }
        long oldPosition = currentPosition;
        byte[] data = readBytes();
        int minLength = Math.min(data.length, b.length);
        System.arraycopy(data, (int) oldPosition, b, 0, minLength);
        currentPosition = oldPosition + minLength;
        if (currentPosition > length){
            currentPosition = length;
        }
        return minLength;
    }

    @Override
    public int read(byte[] b, int off, int len) {
        // TODO !!!! rewrite
        long oldPosition = currentPosition;
        byte[] data = Bytes.fromString(readString());
        len = Math.min(len, data.length);
        System.arraycopy(data, off, b, 0, len);
        currentPosition = oldPosition;
        return len;
    }

    @Override
    public long skip(long n) {
        currentPosition += n;
        return n;
    }

    public long length(){
        return length;
    }
}
