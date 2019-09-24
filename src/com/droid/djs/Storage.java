package com.droid.djs;

import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.nodes.DataInputStream;
import com.droid.djs.nodes.NativeNode;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.ThreadNode;
import com.droid.djs.nodes.Data;
import com.droid.gdb.*;
import com.droid.gdb.map.Crc16;
import com.droid.gdb.map.InfinityHashMap;
import com.droid.instance.Instance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class Storage {

    private static final int MAX_TRANSACTION_CACHE_NODE_COUNT = 10;
    private ArrayList<Node> transactionNodes = new ArrayList<>();
    private Map<Long, Node> nodesCache = new TreeMap<>();
    private InfinityStringArray nodeStorage;

    public Storage(String infinityFileDir, String infinityFileName) {
        nodeStorage = new InfinityStringArray(infinityFileDir, infinityFileName) {
            @Override
            public MetaCell initMeta() {
                return new NodeMetaCell();
            }
        };

        dataStorage = new InfinityFile(Instance.get().storeDir, "data");
        dataHashTree = new InfinityHashMap(Instance.get().storeDir, "hash");
    }

    public boolean isEmpty() {
        return nodeStorage.fileData.sumFilesSize == 0;
    }

    public void addToTransaction(Node node) {
        if (transactionNodes.size() >= MAX_TRANSACTION_CACHE_NODE_COUNT)
            transactionCommit();
        transactionNodes.add(node);
        node.isSaved = true;
        nodesCache.put(node.id, node);
    }

    public void transactionCommit() {
        // TODO change transactionNodes to sync list
        synchronized (transactionNodes) {
            for (Node commitNode : transactionNodes) {
                if (commitNode.id == null)
                    add(commitNode);
                else
                    set(commitNode.id, commitNode);
                commitNode.isSaved = false;
            }
            transactionNodes.clear();
        }
    }

    public Node newNode(NodeType nodeType) {
        switch (nodeType) {
            case BOOLEAN:
            case NUMBER:
            case STRING:
                return new Data(nodeType);
            case NATIVE_FUNCTION:
                return new NativeNode();
            case THREAD:
                return new ThreadNode();
            default:
                return new Node(nodeType);
        }
    }

    public Node get(Long index) {
        Node node = nodesCache.get(index);
        if (node == null) {
            NodeMetaCell metaCell = (NodeMetaCell) nodeStorage.getMeta(index);
            NodeType nodeType = NodeType.values()[metaCell.type];
            node = newNode(nodeType);
            node.id = index;
            node.type = nodeType;
            if (nodeType.ordinal() < NodeType.NODE.ordinal()) {
                ((Data) node).data = new DataInputStream(nodeType, metaCell.start, metaCell.length);
            } else {
                byte[] readiedData = nodeStorage.read(metaCell.start, metaCell.length);
                if (readiedData == null)
                    return null;
                node.parse(readiedData);
            }
            nodesCache.put(index, node);
        }
        return node;
    }

    public void set(long index, Node node) {
        if (node.type.ordinal() >= NodeType.NODE.ordinal())
            nodeStorage.setObject(index, node);
        // else {data is not mutable}
    }


    public void add(Node node) {
        byte[] data = node.build();
        NodeMetaCell metaCell = new NodeMetaCell();
        if (data != null/* && data.length != 0*/) {
            byte[] sector = nodeStorage.dataToSector(data);
            metaCell.type = (byte) node.type.ordinal();
            metaCell.start = nodeStorage.add(sector);
            metaCell.length = data.length;
        }
        node.id = nodeStorage.meta.add(metaCell);
        nodesCache.put(node.id, node);
    }


    public static final int MAX_STORAGE_DATA_IN_DB = 2048;

    private Random random = new Random();
    private InfinityFile dataStorage;
    private InfinityHashMap dataHashTree;

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
                        node.id = nodeStorage.meta.add(nodeMetaCell);
                        node.data = new com.droid.djs.nodes.DataInputStream(node.type, nodeMetaCell.start, nodeMetaCell.length);
                        dataHashTree.put(hashKey, Crc16.hashToBytes(hash), node.id);
                    } else {
                        if (nodeMetaCell.length >= MAX_STORAGE_DATA_IN_DB)
                            file.delete(); // delete read file buffer
                        // TODO return instance from nodes cache
                        nodeMetaCell = (NodeMetaCell) nodeStorage.meta.get(prevNodeId, nodeMetaCell);
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

    public void close() throws IOException {
        dataStorage.close();
        dataHashTree.close();
    }

    public DiskManager getDiskManager() {
        return nodeStorage.diskManager;
    }

    // TODO create transactionNodes for DataStorage and put it to all Storage places
}
