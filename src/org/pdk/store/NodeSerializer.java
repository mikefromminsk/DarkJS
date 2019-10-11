package org.pdk.store;

import org.pdk.store.model.DataOrNode;
import org.pdk.store.model.data.*;
import org.pdk.store.model.data.FileData;
import org.pdk.store.model.data.StringData;
import org.pdk.store.model.node.link.LinkType;
import org.pdk.store.model.node.Node;
import org.simpledb.Bytes;

import java.io.InputStream;
import java.util.*;


public class NodeSerializer extends InputStream {

    private int maxDeepLevel;
    private StringBuilder resultStringBuilder;
    private ArrayList<Long> passedNodeIds = new ArrayList<>();
    private LinkedHashMap<Node, Integer> nextNodes = new LinkedHashMap<>();
    private Storage storage;

    public NodeSerializer(Storage storage, Node node) {
        this(storage, node, 1);
    }

    public NodeSerializer(Storage storage, Node node, int maxDeepLevel) {
        this.storage = storage;
        this.maxDeepLevel = maxDeepLevel;
        this.resultStringBuilder = new StringBuilder("{\n");
        nextNodes.put(node, 0);
    }

    private void appendDon(DataOrNode don, int deepLevel) {
        if (don instanceof Data) {
            Data data = (Data) don;
            if (data instanceof BooleanData) {
                resultStringBuilder.append(((BooleanData) data).value);
            } else if (data instanceof NumberData) {
                resultStringBuilder.append(((NumberData) data).number);
            } else if (data instanceof StringData) {
                resultStringBuilder.append("\"!").append(Bytes.toString(((StringData) data).bytes)).append("\"");
            } else if (data instanceof FileData) {
                resultStringBuilder.append("\"@").append(((FileData) data).fileId).append("\"");
            }
        } else {
            Node node = (Node) don;
            resultStringBuilder.append("\"n").append(node.nodeId).append("\"");
            if (deepLevel < maxDeepLevel)
                nextNodes.putIfAbsent(node, deepLevel + 1);
        }
    }

    public void appendNode(Node node, Integer deepLevel) {
        if (passedNodeIds.contains(node.nodeId)) return;
        resultStringBuilder.append("\"n").append(node.nodeId).append("\" :{\n");
        node.listLinks((linkType, link, singleValue) -> {
            if (linkType == LinkType.LOCAL_PARENT) return;

            String linkName = linkType.toString().toLowerCase();
            resultStringBuilder.append("\t\"").append(linkName);
            if (singleValue) {
                resultStringBuilder.append("\": ");
                appendDon(link instanceof Long ? storage.get((Long) link) : (DataOrNode) link, deepLevel);
                resultStringBuilder.append(",\n");
            } else {
                ArrayList<Object> links = (ArrayList<Object>) link;
                resultStringBuilder.append("\": [\n");
                for (Object item : links) {
                    resultStringBuilder.append("\t\t");
                    appendDon(item instanceof Long ? storage.get((Long) item) : (DataOrNode) item, deepLevel);
                    resultStringBuilder.append(",\n");
                }
                resultStringBuilder.append("\t],\n");
            }
        });
        resultStringBuilder.append("},\n");
    }

    private byte[] readByByteBuffer;

    @Override
    public int read() {
        if (readByByteBuffer == null)
            readByByteBuffer = new byte[1];
        if (read(readByByteBuffer) != -1)
            return (int) readByByteBuffer[0];
        return -1;
    }

    @Override
    public int read(byte[] outBuffer) {
        if (nextNodes.size() == 0 && resultStringBuilder.length() == 0)
            return -1;
        while (resultStringBuilder.length() < outBuffer.length) {
            Map.Entry<Node, Integer> firstRecord = nextNodes.entrySet().iterator().next();
            Node node = firstRecord.getKey();
            Integer deepLevel = firstRecord.getValue();
            appendNode(node, deepLevel);
            nextNodes.remove(node);
            if (nextNodes.size() == 0)
                resultStringBuilder.append("}");
            passedNodeIds.add(node.nodeId);
        }
        byte[] resultBytes = resultStringBuilder.toString().getBytes();
        int minLength = Math.min(resultBytes.length, outBuffer.length);
        System.arraycopy(resultBytes, 0, outBuffer, 0, minLength);
        resultStringBuilder.delete(0, minLength);
        return minLength;
    }
}
