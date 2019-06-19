package com.droid.djs;

import com.droid.djs.consts.NodeType;
import com.droid.djs.nodes.DataInputStream;
import com.droid.djs.nodes.NativeNode;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.ThreadNode;
import com.droid.djs.runner.prototypes.Prototypes;
import com.droid.djs.runner.utils.UtilList;
import com.droid.gdb.*;
import com.droid.gdb.map.Crc16;
import com.droid.gdb.map.InfinityHashMap;
import com.droid.djs.fs.Master;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class NodeStorage extends InfinityStringArray {

    private static NodeStorage instance;

    private static final String passStorageID = "password";
    private static final String nodeStorageID = "node";
    private static final String dataStorageID = "data";
    private static final String hashStorageID = "hash";

    public static final int MAX_STORAGE_DATA_IN_DB = 2048;
    private static final int MAX_TRANSACTION_CACHE_NODE_COUNT = 10;
    private static InfinityFile dataStorage;
    private static InfinityHashMap dataHashTree;
    private static ArrayList<Node> transactionNodes;
    private static Map<Long, Node> nodesCache = new TreeMap<>();


    public NodeStorage(String infinityFileID) {
        super(infinityFileID);
        if (meta.fileData.sumFilesSize == 0)
            initStorage();
    }

    private void initStorage() {
        ThreadNode root = new ThreadNode();
        add(root);
        transactionCommit();
        Master.getInstance();
        Prototypes.getInstance();
        UtilList.init();
    }

    public static NodeStorage getInstance() {
        if (instance == null) {
            instance = new NodeStorage(nodeStorageID);

            transactionNodes = new ArrayList<>();
            dataStorage = new InfinityFile(dataStorageID);
            dataHashTree = new InfinityHashMap(hashStorageID);
        }
        return instance;
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

    class NodeMetaCell extends MetaCell {

        private final static int META_CELL_SIZE = Byte.BYTES + 3 * Long.BYTES;
        public byte type;

        @Override
        public void parse(byte[] data) {
            ByteBuffer bytebuffer = ByteBuffer.wrap(data);
            type = bytebuffer.get();
            start = bytebuffer.getLong();
            length = bytebuffer.getLong();
            accessKey = bytebuffer.getLong();
        }

        @Override
        public byte[] build() {
            ByteBuffer bytebuffer = ByteBuffer.allocate(META_CELL_SIZE);
            bytebuffer.put(type);
            bytebuffer.putLong(start);
            bytebuffer.putLong(length);
            bytebuffer.putLong(accessKey);
            return bytebuffer.array();
        }

        @Override
        public int getSize() {
            return META_CELL_SIZE;
        }
    }

    @Override
    public MetaCell initMeta() {
        return new NodeMetaCell();
    }

    public Node newNode(byte nodeType) {
        switch (nodeType) {
            case NodeType.NATIVE_FUNCTION:
                return new NativeNode();
            case NodeType.THREAD:
                return new ThreadNode();
            default:
                return new Node(nodeType);
        }
    }

    public Node get(Long index) {
        Node node = nodesCache.get(index);
        if (node == null) {
            NodeMetaCell metaCell = (NodeMetaCell) getMeta(index);
            node = newNode(metaCell.type);
            node.id = index;
            node.type = metaCell.type;
            if (metaCell.type < NodeType.VAR) {
                node.data = new DataInputStream(this, metaCell.type, metaCell.start, metaCell.length);
            } else {
                byte[] readiedData = read(metaCell.start, metaCell.length);
                if (readiedData == null)
                    return null;
                decodeData(readiedData, metaCell.accessKey);
                node.parse(readiedData);
            }
            nodesCache.put(index, node);
        }
        return node;
    }

    public void set(long index, Node node) {
        if (node.type >= NodeType.VAR)
            super.setObject(index, node);
        // else {data is not mutable}
    }

    private static Random random = new Random();

    public void add(Node node) {
        if (node.type >= NodeType.VAR) {
            byte[] data = node.build();
            NodeMetaCell metaCell = new NodeMetaCell();
            if (data != null/* && data.length != 0*/) {
                byte[] sector = dataToSector(data);
                long newAccessKey = encodeData(sector);
                metaCell.type = node.type;
                metaCell.start = super.add(sector);
                metaCell.length = data.length;
                metaCell.accessKey = newAccessKey;
            }
            node.id = meta.add(metaCell);
        } else {
            try {
                if (node.externalData != null) {
                    Reader in = new InputStreamReader(node.externalData);
                    byte[] hashKey = null;
                    int hash = 0;
                    OutputStream outStream = null;

                    NodeMetaCell nodeMetaCell = new NodeMetaCell();
                    nodeMetaCell.type = node.type;
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
                            node.id = meta.add(nodeMetaCell);
                            node.data = new DataInputStream(this, nodeMetaCell.type, nodeMetaCell.start, nodeMetaCell.length);
                            node.externalData = null;
                            dataHashTree.put(hashKey, Crc16.hashToBytes(hash), node.id);
                        } else {
                            if (nodeMetaCell.length >= MAX_STORAGE_DATA_IN_DB)
                                file.delete(); // delete read file buffer
                            // TODO return instance from nodes cache
                            nodeMetaCell = (NodeMetaCell) meta.get(prevNodeId, nodeMetaCell);
                            node.id = prevNodeId;
                            node.data = new DataInputStream(this, nodeMetaCell.type, nodeMetaCell.start, nodeMetaCell.length);
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
    }

    // TODO change getData function name
    public byte[] getData(long start, long offset, int length) {
        return dataStorage.read(start + offset, length);
    }

    public Long getDataId(byte[] title) {
        if (title != null)
            return dataHashTree.get(title, Crc16.getHashBytes(title));
        return null;
    }

    public Long put(byte[] title) {
        if (title != null)
            return dataHashTree.get(title, Crc16.getHashBytes(title));
        return null;
    }
}
