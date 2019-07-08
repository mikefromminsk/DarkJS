package com.droid.djs;

import com.droid.djs.nodes.DataInputStream;
import com.droid.djs.nodes.Data;
import com.droid.gdb.Bytes;
import com.droid.gdb.DiskManager;
import com.droid.gdb.InfinityFile;
import com.droid.gdb.map.Crc16;
import com.droid.gdb.map.InfinityHashMap;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class DataStorage {

    public static final int MAX_STORAGE_DATA_IN_DB = 2048;

    private static final String dataStorageID = "data";
    private static final String hashStorageID = "hash";

    private static InfinityFile dataStorage;
    private static InfinityHashMap dataHashTree;

    private static DataStorage instance;

    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();

            dataStorage = new InfinityFile(dataStorageID);
            dataHashTree = new InfinityHashMap(hashStorageID);

        }
        return instance;
    }

    private static Random random = new Random();

    public void add(Data node){
        try {
            if (node.externalData != null) {
                Reader in = new InputStreamReader(node.externalData);
                byte[] hashKey = null;
                int hash = 0;
                OutputStream outStream = null;

                NodeMetaCell nodeMetaCell = new NodeMetaCell();
                nodeMetaCell.type = (byte) node.type.ordinal();
                nodeMetaCell.length = 0;
                char[] buffer = new char[MAX_STORAGE_DATA_IN_DB];
                byte[] bytes = null;
                int readiedBytes;
                File file = null;
                while ((readiedBytes = in.read(buffer)) != -1) {
                    bytes = Bytes.fromCharArray(Arrays.copyOfRange(buffer, 0, readiedBytes));
                    hash = Crc16.getHash(hash, bytes);
                    nodeMetaCell.length += readiedBytes;
                    if (outStream == null) {
                        hashKey = bytes;
                        if (readiedBytes == MAX_STORAGE_DATA_IN_DB) {
                            nodeMetaCell.start = random.nextLong();
                            file = DiskManager.getInstance().getFileById(nodeMetaCell.start);
                            if (!file.exists())
                                file.createNewFile();
                            outStream = new FileOutputStream(file, false);
                            outStream.write(bytes);
                        }
                    } else {
                        outStream.write(bytes);
                    }
                }
                if (outStream != null)
                    outStream.close();
                if (hashKey != null) {
                    long prevNodeId = dataHashTree.get(hashKey, Crc16.hashToBytes(hash));
                    if (prevNodeId == Long.MAX_VALUE) {
                        if (nodeMetaCell.length < MAX_STORAGE_DATA_IN_DB) {
                            nodeMetaCell.start = dataStorage.add(bytes);
                        }
                        node.id = NodeStorage.getInstance().meta.add(nodeMetaCell);
                        node.data = new com.droid.djs.nodes.DataInputStream(node.type, nodeMetaCell.start, nodeMetaCell.length);
                        node.externalData = null;
                        dataHashTree.put(hashKey, Crc16.hashToBytes(hash), node.id);
                    } else {
                        if (nodeMetaCell.length >= MAX_STORAGE_DATA_IN_DB)
                            file.delete(); // delete read file buffer
                        // TODO return instance from nodes cache
                        nodeMetaCell = (NodeMetaCell) NodeStorage.getInstance().meta.get(prevNodeId, nodeMetaCell);
                        node.id = prevNodeId;
                        node.data = new DataInputStream( node.type, nodeMetaCell.start, nodeMetaCell.length);
                        node.externalData = null;
                    }
                } else {
                    // TODO when inputData.length == 0
                }
                in.close();
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
        if (title != null)
            return dataHashTree.get(title, Crc16.getHashBytes(title));
        return null;
    }
}
