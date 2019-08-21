package com.droid.djs;

import com.droid.djs.nodes.DataInputStream;
import com.droid.djs.nodes.Data;
import com.droid.gdb.Bytes;
import com.droid.gdb.DiskManager;
import com.droid.gdb.InfinityFile;
import com.droid.gdb.map.Crc16;
import com.droid.gdb.map.InfinityHashMap;
import com.droid.instance.Instance;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class DataStorage {

    public static final int MAX_STORAGE_DATA_IN_DB = 2048;

    private Random random = new Random();
    private InfinityFile dataStorage;
    private InfinityHashMap dataHashTree;

    public DataStorage() {
        dataStorage = new InfinityFile(Instance.get().storeDir, "data");
        dataHashTree = new InfinityHashMap(Instance.get().storeDir, "hash");
    }

    public void add(Data node) {
        try {
            if (node.externalData != null) {
                byte[] hashKey = null;
                int hash = 0;
                OutputStream outStream = null;

                NodeMetaCell nodeMetaCell = new NodeMetaCell();
                nodeMetaCell.type = (byte) node.type.ordinal();
                nodeMetaCell.length = 0;
                byte[] buffer = new byte[MAX_STORAGE_DATA_IN_DB];
                int readiedBytes;
                File file = null;
                while ((readiedBytes = node.externalData.read(buffer)) != -1) {
                    hash = Crc16.getHash(hash, buffer);
                    nodeMetaCell.length += readiedBytes;
                    if (outStream == null) {
                        hashKey = buffer;
                        if (readiedBytes == MAX_STORAGE_DATA_IN_DB) {
                            nodeMetaCell.start = random.nextLong();
                            file = DiskManager.getInstance(Instance.get().storeDir).getFileById(nodeMetaCell.start);
                            if (!file.exists())
                                file.createNewFile();
                            outStream = new FileOutputStream(file);
                            outStream.write(buffer);
                        }
                    } else {
                        outStream.write(buffer, 0, readiedBytes);
                    }
                }
                if (outStream != null)
                    outStream.close();
                if (hashKey != null) {
                    long prevNodeId = dataHashTree.get(hashKey, Crc16.hashToBytes(hash));
                    if (prevNodeId == Long.MAX_VALUE) {
                        if (nodeMetaCell.length < MAX_STORAGE_DATA_IN_DB) {
                            nodeMetaCell.start = dataStorage.add(buffer);
                        }
                        node.id = Instance.get().getNodeStorage().meta.add(nodeMetaCell);
                        node.data = new com.droid.djs.nodes.DataInputStream(node.type, nodeMetaCell.start, nodeMetaCell.length);
                        dataHashTree.put(hashKey, Crc16.hashToBytes(hash), node.id);
                    } else {
                        if (nodeMetaCell.length >= MAX_STORAGE_DATA_IN_DB)
                            file.delete(); // delete read file buffer
                        // TODO return instance from nodes cache
                        nodeMetaCell = (NodeMetaCell) Instance.get().getNodeStorage().meta.get(prevNodeId, nodeMetaCell);
                        node.id = prevNodeId;
                        node.data = new DataInputStream(node.type, nodeMetaCell.start, nodeMetaCell.length);
                    }
                }
                node.externalData.close();
                node.externalData = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO change getData function name
    // TODO public byte[] getData(byte[] buf) {
    public byte[] getData(long start, long offset, int length) {
        return dataStorage.read(start + offset, length);
    }

    public Long getDataId(byte[] title) {
        if (title != null) {
            long titleId = dataHashTree.get(title, Crc16.getHashBytes(title));
            if (titleId == Long.MAX_VALUE)
                return null;
        }
        return null;
    }
}
