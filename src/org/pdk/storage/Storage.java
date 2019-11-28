package org.pdk.storage;

import org.pdk.converters.ConverterManager;
import org.pdk.storage.model.node.Node;
import org.simpledb.InfinityStringArray;
import org.simpledb.MetaCell;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Storage {

    private final static int MAX_CACHE_NODE_COUNT = 10000;
    private final static int MAX_TRANSACTION_NODE_COUNT = 10000;
    public final String storeDir;
    public ConverterManager converterManager = new ConverterManager(this);

    private ArrayList<Node> transactionNodes = new ArrayList<>();
    private Map<Long, Node> nodesCache = new TreeMap<>();

    private InfinityStringArray nodeStorage;

    public Storage(String storeDir, boolean removeDir) {
        if (!storeDir.endsWith("\\") && !storeDir.endsWith("/"))
            storeDir += "/";
        this.storeDir = storeDir;
        if (removeDir)
            deleteDirectory(new File(storeDir));
        nodeStorage = new InfinityStringArray(storeDir, "node");
        if (nodeStorage.fileData.sumFilesSize == 0)
            new NodeBuilder(this).create().commit();
    }

    void deleteDirectory(File directoryToBeDeleted) {
        File[] files = directoryToBeDeleted.listFiles();
        if (files != null) {
            for (File file : files)
                if (file.isDirectory()) {
                    deleteDirectory(file);
                    directoryToBeDeleted.delete();
                }
                else
                    file.delete();
        }
    }

    public Node get(Long nodeId) {
        Node node = nodesCache.get(nodeId);
        if (node == null) {
            MetaCell metaCell = nodeStorage.getMeta(nodeId);
            node = new Node(this);
            byte[] readiedData = nodeStorage.read(metaCell.start, metaCell.length);
            node.parse(readiedData);
            node.nodeId = nodeId;
        }
        return node;
    }

    public void addToTransaction(Node node) {
        if (!node.isSaved) {
            if (transactionNodes.size() >= MAX_TRANSACTION_NODE_COUNT)
                transactionCommit();
            transactionNodes.add(node);
            node.isSaved = true;
        }
    }

    public void addToCache(Node node) {
        nodesCache.put(node.nodeId, node);
        if (nodesCache.keySet().size() > MAX_CACHE_NODE_COUNT) {
            nodesCache.clear();
            // TODO add logic of removing
        }
    }

    public void transactionCommit() {
        // TODO change transactionNodes to sync list
        synchronized (transactionNodes) {
            for (Node node : transactionNodes) {
                nodeStorage.setObject(node.nodeId, node);
                node.isSaved = false;
            }
            transactionNodes.clear();
        }
    }

    public Long newNodeId() {
        return nodeStorage.meta.add(new MetaCell());
    }

    /*public void close() throws IOException {
        transactionCommit();
        nodeStorage.close();
        dataStorage.close();
        dataHashTree.close();
    }*/
}
