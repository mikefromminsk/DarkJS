package org.pdk.store;

import org.pdk.store.model.node.NativeNode;
import org.pdk.store.model.node.Node;
import org.pdk.store.model.node.ThreadNode;
import org.pdk.store.model.node.meta.NodeMeta;
import org.pdk.store.model.node.meta.NodeType;
import org.simpledb.Bytes;
import org.simpledb.InfinityFile;
import org.simpledb.InfinityStringArray;
import org.simpledb.MetaCell;
import org.simpledb.map.InfinityHashMap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Storage {

    private static final int MAX_TRANSACTION_CACHE_NODE_COUNT = 10;

    private ArrayList<Node> transactionNodes = new ArrayList<>();
    private Map<Long, Node> nodesCache = new TreeMap<>();

    private InfinityStringArray nodeStorage;
    private InfinityFile dataStorage;
    private InfinityHashMap dataHashTree;

    public Storage(String storeDir) {
        nodeStorage = new InfinityStringArray(storeDir, "node");
        dataStorage = new InfinityFile(storeDir, "data");
        dataHashTree = new InfinityHashMap(storeDir, "hash");
    }

    public Node get(Long nodeId) {
        Node node = nodesCache.get(nodeId);
        if (node == null) {
            NodeMeta metaCell = (NodeMeta) nodeStorage.getMeta(nodeId);
            node = createNodeInstance(metaCell.type);
            byte[] readiedData = nodeStorage.read(metaCell.start, metaCell.length);
            node.parse(readiedData);
            node.nodeId = nodeId;
            nodesCache.put(nodeId, node);
        }
        return node;
    }

    public Node createNodeInstance(NodeType nodeType) {
        switch (nodeType) {
            case THREAD:
                return new ThreadNode();
            case NATIVE_FUNCTION:
                return new NativeNode();
            default:
                return new Node(this);
        }
    }

    private NodeType getNodeType(Node node) {
        if (node instanceof ThreadNode) {
            return NodeType.NATIVE_FUNCTION;
        } else if (node instanceof NativeNode) {
            return NodeType.NATIVE_FUNCTION;
        } else {
            return NodeType.NODE;
        }
    }

    public void transactionCommit() {
        // TODO change transactionNodes to sync list
        synchronized (transactionNodes) {
            for (Node node : transactionNodes) {
                if (node.nodeId == null) {
                    NodeMeta metaCell = new NodeMeta();
                    byte[] data = node.build();
                    if (data.length != 0) {
                        byte[] sector = nodeStorage.dataToSector(data);
                        metaCell.type = getNodeType(node);
                        metaCell.start = nodeStorage.add(sector);
                        metaCell.length = data.length;
                    }
                    node.nodeId = nodeStorage.meta.add(metaCell);
                    nodesCache.put(node.nodeId, node);
                } else
                    nodeStorage.setObject(node.nodeId, node);
                node.isSaved = false;
            }
            transactionNodes.clear();
        }
    }

    public void addToTransaction(Node node) {
        if (!node.isSaved) {
            if (transactionNodes.size() >= MAX_TRANSACTION_CACHE_NODE_COUNT)
                transactionCommit();
            transactionNodes.add(node);
            node.isSaved = true;
            nodesCache.put(node.nodeId, node);
        }
    }

    long putString(byte[] bytes){
    }

    public void close() throws IOException {
        transactionCommit();
        nodeStorage.close();
        dataStorage.close();
        dataHashTree.close();
    }

    public boolean isEmpty() {
        return nodeStorage.fileData.sumFilesSize == 0;
    }

    public long putString(byte[] bytes, int sectorLength) {
        ByteBuffer bb = ByteBuffer.allocate(sectorLength);
        bb.
        return Bytes.concat(Bytes.long)dataStorage.add(bytes);
    }
}
