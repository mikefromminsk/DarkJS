package org.pdk.store;

import org.pdk.store.model.DataOrNode;
import org.pdk.store.model.data.*;
import org.pdk.store.model.node.LinkType;
import org.pdk.store.model.node.Node;

import java.io.InputStream;
import java.util.ArrayList;


public class NodeSerializer extends InputStream {

    public static final String NODE_PREFIX = "n";
    public static final String TYPE_KEY = "type";
    public static final String DATA_KEY = "data";
    public static final String STRING_PREFIX = "!";
    public static final String LINK_PREFIX = "@";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    private Node node;
    private Node current;
    private int level;
    private NodeBuilder builder;
    private StringBuilder result;
    private ArrayList<Long> passed = new ArrayList<>();
    private ArrayList<Node> next = new ArrayList<>();

    public NodeSerializer(Node node) {
        this(node, 1);
    }

    public NodeSerializer(Node node, int level) {
        this.node = node;
        this.level = level;
        this.result = new StringBuilder("{\n");
    }

    private void appendDon(DataOrNode don) {
        if (don instanceof Data) {
            Data data = (Data) don;
            if (data instanceof BooleanData) {
                result.append(((BooleanData) data).bool);
            } else if (data instanceof NumberData) {
                result.append(((NumberData) data).number);
            } else if (data instanceof StringData) {
                result.append("\"!").append(((StringData) data).bytes).append("\"");
            } else if (data instanceof FileData) {
                result.append("\"@").append(((FileData) data).id).append("\"");
            }
        } else {
            Node node = (Node) don;
            result.append("\"n").append(node.id).append("\"");
        }
    }

    public void toMapRecursive(Node don) {
        if (don != null) {
            if (passed.indexOf(node.id) != -1) return;

            String nodeName = NODE_PREFIX + node.id;

            result.append(nodeName).append('\n');

            //if (node instanceof ThreadNode)

            node.listLinks((linkType, link, singleValue) -> {
                if (linkType == LinkType.LOCAL_PARENT) return;

                /*if (linkType == LinkType.NATIVE_FUNCTION) {
                    String linkTypeStr = linkType.toString().toLowerCase();
                    FuncInterface funcInterface = Module.getFunctionInterface((int) (long) link);
                    links.put(linkTypeStr, "!" + funcInterface.path + funcInterface.name);
                    return;
                }*/
                // Nodes links
                String linkName = linkType.toString().toLowerCase();
                result.append("\t\"").append(linkName);
                if (singleValue) {
                    result.append("\": ");
                    appendDon(link instanceof Long ? builder.get((Long) link).() : (DataOrNode) link);
                    result.append(",\n");
                } else {
                    ArrayList<Object> links = (ArrayList<Object>) link;
                    result.append("\": [\n");
                    for (Object item : links) {
                        appendDon(item instanceof Long ? builder.get((Long) item).getDon() : (DataOrNode) link);
                        result.append(",\n");
                    }
                    result.append("],\n");
                }
            });
        }
    }

    /* ignore this method*/
    @Override
    public int read() {
        return 0;
    }


    @Override
    public int read(byte[] buffer) {
        while (result.length() < buffer.length) {
            toMapRecursive(node);
        }
        result.append("}");
        if (result.length() < buffer.length) {

            return result.length();
        } else {

            return buffer.length;
        }
    }
}
