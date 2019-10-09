package org.pdk.store;

import org.pdk.files.converters.ConverterManager;
import org.pdk.store.model.node.Node;
import org.simpledb.InfinityFile;
import org.simpledb.InfinityStringArray;
import org.simpledb.MetaCell;
import org.simpledb.map.InfinityHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Storage {

    private int MAX_TRANSACTION_CACHE_NODE_COUNT = 1000;
    public final String storeDir;
    public ConverterManager converterManager = new ConverterManager(this);

    private ArrayList<Node> transactionNodes = new ArrayList<>();
    private Map<Long, Node> nodesCache = new TreeMap<>();

    private InfinityStringArray nodeStorage;
    private InfinityFile dataStorage;
    private InfinityHashMap dataHashTree;

    public Storage(String storeDir) {
        if (!storeDir.endsWith("\\") && !storeDir.endsWith("/"))
            storeDir += "/";
        this.storeDir = storeDir;
        nodeStorage = new InfinityStringArray(storeDir, "node");
        dataStorage = new InfinityFile(storeDir, "data");
        dataHashTree = new InfinityHashMap(storeDir, "hash");
    }

    public Node get(Long nodeId) {
        Node node = nodesCache.get(nodeId);
        if (node == null) {
            MetaCell metaCell = nodeStorage.getMeta(nodeId);
            node = new Node(this);
            byte[] readiedData = nodeStorage.read(metaCell.start, metaCell.length);
            node.parse(readiedData);
            node.nodeId = nodeId;
            nodesCache.put(nodeId, node);
        }
        return node;
    }

    public void transactionCommit() {
        // TODO change transactionNodes to sync list
        synchronized (transactionNodes) {
            for (Node node : transactionNodes) {
                if (node.nodeId == null) {
                    MetaCell metaCell = new MetaCell();
                    byte[] data = node.build();
                    if (data.length != 0) {
                        byte[] sector = nodeStorage.dataToSector(data);
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

    public void close() throws IOException {
        transactionCommit();
        nodeStorage.close();
        dataStorage.close();
        dataHashTree.close();
    }

    public boolean isEmpty() {
        return nodeStorage.fileData.sumFilesSize == 0;
    }
}
