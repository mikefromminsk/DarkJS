package org.pdk.store;

import org.pdk.store.model.node.Node;
import org.pdk.store.model.node.meta.NodeMeta;
import org.pdk.store.model.node.meta.NodeType;
import org.simpledb.InfinityFile;
import org.simpledb.InfinityStringArray;
import org.simpledb.MetaCell;
import org.simpledb.map.InfinityHashMap;

import java.io.IOException;
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
        nodeStorage = new InfinityStringArray(storeDir, "node") {
            @Override
            public MetaCell initMeta() {
                return new NodeMeta();
            }
        };
        dataStorage = new InfinityFile(storeDir, "data");
        dataHashTree = new InfinityHashMap(storeDir, "hash");
    }

    public Node get(Long index) {
        Node node = nodesCache.get(index);
        if (node == null) {
            NodeMeta metaCell = (NodeMeta) nodeStorage.getMeta(index);
            node = createNodeInstance(metaCell.type);
            byte[] readiedData = nodeStorage.read(metaCell.start, metaCell.length);
            node.parse(readiedData);
            node.id = index;
            nodesCache.put(index, node);
        }
        return node;
    }

    private Node createNodeInstance(NodeType nodeType) {
        switch (nodeType) {
            case NATIVE_FUNCTION:
                //return new NativeFunction(); break;
            default:
                return new Node();
        }
    }

    private NodeType getNodeType(Node node){
        /*if (node instanceof NativeNode){

        }else*/{
            return NodeType.NODE;
        }
    }

    public void transactionCommit() {
        // TODO change transactionNodes to sync list
        synchronized (transactionNodes) {
            for (Node node : transactionNodes) {
                if (node.id == null){
                    NodeMeta metaCell = new NodeMeta();
                    byte[] data = node.build();
                    if (data.length != 0) {
                        byte[] sector = nodeStorage.dataToSector(data);
                        metaCell.type = getNodeType(node);
                        metaCell.start = nodeStorage.add(sector);
                        metaCell.length = data.length;
                    }
                    node.id = nodeStorage.meta.add(metaCell);
                    nodesCache.put(node.id, node);
                }
                else {
                    nodeStorage.setObject(node.id, node);
                }
                node.isSaved = false;
            }
            transactionNodes.clear();
        }
    }

    public void addToTransaction(Node node) {
        if (transactionNodes.size() >= MAX_TRANSACTION_CACHE_NODE_COUNT)
            transactionCommit();
        transactionNodes.add(node);
        node.isSaved = true;
        nodesCache.put(node.id, node);
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
