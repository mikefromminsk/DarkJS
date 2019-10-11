package org.pdk.store;

import org.pdk.store.model.DataOrNode;
import org.pdk.store.model.data.*;
import org.pdk.store.model.data.FileData;
import org.pdk.store.model.data.StringData;
import org.pdk.store.model.node.link.LinkType;
import org.pdk.store.model.node.Node;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class NodeSerializer extends InputStream {

    public static final String NODE_PREFIX = "n";
    public static final String TYPE_KEY = "type";
    public static final String DATA_KEY = "data";
    public static final String STRING_PREFIX = "!";
    public static final String LINK_PREFIX = "@";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private int maxLevel;
    private StringBuilder result;
    private ArrayList<Long> passed = new ArrayList<>();
    private LinkedHashMap<Node, Integer> next = new LinkedHashMap<>();
    Storage storage;

    public NodeSerializer(Storage storage, Node node) {
        this(storage, node, 1);
    }

    public NodeSerializer(Storage storage, Node node, int maxLevel) {
        this.storage = storage;
        this.maxLevel = maxLevel;
        this.result = new StringBuilder("{\n");
        next.put(node, 0);
    }

    private void appendDon(DataOrNode don, int currentLevel) {
        if (don instanceof Data) {
            Data data = (Data) don;
            if (data instanceof BooleanData) {
                result.append(((BooleanData) data).value);
            } else if (data instanceof NumberData) {
                result.append(((NumberData) data).number);
            } else if (data instanceof StringData) {
                result.append("\"!").append(((StringData) data).bytes).append("\"");
            } else if (data instanceof FileData) {
                result.append("\"@").append(((FileData) data).fileId).append("\"");
            }
        } else {
            Node node = (Node) don;
            result.append("\"n").append(node.nodeId).append("\"");
            if (currentLevel < maxLevel)
                next.putIfAbsent(node, currentLevel + 1);
        }
    }

    public void appendNode(Map.Entry<Node, Integer> nodeEntry) {
        Node node = nodeEntry.getKey();
        Integer currentLevel = nodeEntry.getValue();
        if (passed.indexOf(node.nodeId) != -1) return;
        result.append("n").append(node.nodeId).append(" :{\n");
        node.listLinks((linkType, link, singleValue) -> {
            if (linkType == LinkType.LOCAL_PARENT) return;

            String linkName = linkType.toString().toLowerCase();
            result.append("\t\"").append(linkName);
            if (singleValue) {
                result.append("\": ");
                appendDon(link instanceof Long ? storage.get((Long) link) :(DataOrNode) link, currentLevel);
                result.append(",\n");
            } else {
                ArrayList<Object> links = (ArrayList<Object>) link;
                result.append("\": [\n");
                for (Object item : links) {
                    appendDon(item instanceof Long ? storage.get((Long) link) : (DataOrNode) link, currentLevel);
                    result.append(",\n");
                }
                result.append("],\n");
            }
        });
        result.append("},\n");
    }

    /* ignore this method*/
    @Override
    public int read() {
        return 0;
    }


    @Override
    public int read(byte[] buffer) {
        if (next.size() == 0)
            return 0;
        while (result.length() < buffer.length) {
            Iterator<Map.Entry<Node, Integer>> it = next.entrySet().iterator();
            Map.Entry<Node, Integer> entry = it.next();
            appendNode(entry);
            passed.add(entry.getKey().nodeId);
            it.remove();
        }
        if (next.size() == 0)
            result.append("}");
        byte[] bytes = result.toString().getBytes();
        int length = Math.min(bytes.length, buffer.length);
        System.arraycopy(bytes, 0, buffer, 0, length);
        result.delete(0, length - 1);
        return length;
    }
}
