package org.pdk.storage.model.node;

import org.pdk.funcitons.Function;
import org.pdk.funcitons.FunctionManager;
import org.pdk.storage.Storage;
import org.pdk.storage.model.DataOrNode;
import org.pdk.storage.model.data.BooleanData;
import org.pdk.storage.model.data.FileData;
import org.pdk.storage.model.data.NumberData;
import org.pdk.storage.model.data.StringData;
import org.pdk.storage.model.node.link.LinkDataType;
import org.pdk.storage.model.node.link.LinkType;
import org.pdk.storage.model.node.link.NodeLinkListener;
import org.simpledb.InfinityStringArrayCell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Node implements InfinityStringArrayCell, DataOrNode {

    public boolean isSaved;
    public Long nodeId;
    public Function function;
    public Thread thread;
    public Object value;
    public Object source;
    public StringData title;
    public Object set;
    public Object _true;
    public Object _else;
    public Object exit;
    public Object _while;
    public Object _if;
    public Object prototype;
    public Object localParent;
    public StringData parser;

    public ArrayList<Object> local;
    public ArrayList<Object> param;
    public ArrayList<Object> next;
    public ArrayList<Object> cell;
    public ArrayList<Object> prop;
    // after addObject new link you should addObject it to listLinks and build function

    private Storage storage;

    public Node(Storage storage) {
        this.storage = storage;
    }

    @Override
    public byte[] build() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (function != null) {
            try {
                ByteBuffer bb = ByteBuffer.allocate(6);
                bb.put((byte) LinkType.NATIVE_FUNCTION.ordinal());
                bb.put((byte) LinkDataType.NATIVE_FUNCTION_PATH.ordinal());
                String functionPath = function.path();
                bb.putInt(functionPath.length());
                baos.write(functionPath.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        listLinks((linkType, link, singleValue) -> {
            try {
                if (link instanceof Long) {
                    ByteBuffer bb = ByteBuffer.allocate(10);
                    bb.put((byte) linkType.ordinal());
                    bb.put((byte) LinkDataType.NODE.ordinal());
                    bb.putLong((Long) link);
                    baos.write(bb.array());
                } else if (link instanceof Node) {
                    ByteBuffer bb = ByteBuffer.allocate(10);
                    bb.put((byte) linkType.ordinal());
                    bb.put((byte) LinkDataType.NODE.ordinal());
                    bb.putLong(((Node) link).nodeId);
                    baos.write(bb.array());
                } else if (link instanceof BooleanData) {
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.put((byte) linkType.ordinal());
                    if (((BooleanData) link).value)
                        bb.put((byte) LinkDataType.BOOLEAN_TRUE.ordinal());
                    else
                        bb.put((byte) LinkDataType.BOOLEAN_FALSE.ordinal());
                    baos.write(bb.array());
                } else if (link instanceof NumberData) {
                    ByteBuffer bb = ByteBuffer.allocate(10);
                    bb.put((byte) linkType.ordinal());
                    bb.put((byte) LinkDataType.NUMBER.ordinal());
                    bb.putDouble(((NumberData) link).number);
                    baos.write(bb.array());
                } else if (link instanceof FileData) {
                    ByteBuffer bb = ByteBuffer.allocate(6);
                    bb.put((byte) linkType.ordinal());
                    bb.put((byte) LinkDataType.FILE.ordinal());
                    bb.putInt(((FileData) link).fileId);
                    baos.write(bb.array());
                } else if (link instanceof StringData) {
                    ByteBuffer bb = ByteBuffer.allocate(6);
                    bb.put((byte) linkType.ordinal());
                    bb.put((byte) LinkDataType.STRING.ordinal());
                    byte[] bytes = ((StringData) link).bytes;
                    bb.putInt(bytes.length);
                    baos.write(bb.array());
                    baos.write(bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return baos.toByteArray();
    }

    public void listLinks(NodeLinkListener linkListener) {
        if (linkListener == null)
            return;
        // sequences for well see in NodeSerializer
        if (title != null)
            linkListener.get(LinkType.TITLE, title, true);
        if (value != null)
            linkListener.get(LinkType.VALUE, value, true);
        if (source != null)
            linkListener.get(LinkType.SOURCE, source, true);
        if (set != null)
            linkListener.get(LinkType.SET, set, true);
        if (_true != null)
            linkListener.get(LinkType.TRUE, _true, true);
        if (_else != null)
            linkListener.get(LinkType.ELSE, _else, true);
        if (exit != null)
            linkListener.get(LinkType.EXIT, exit, true);
        if (_while != null)
            linkListener.get(LinkType.WHILE, _while, true);
        if (_if != null)
            linkListener.get(LinkType.IF, _if, true);
        if (prototype != null)
            linkListener.get(LinkType.PROTOTYPE, prototype, true);
        if (localParent != null)
            linkListener.get(LinkType.LOCAL_PARENT, localParent, true);
        if (parser != null)
            linkListener.get(LinkType.PARSER, parser, true);
        if (local != null)
            linkListener.get(LinkType.LOCAL, local, false);
        if (param != null)
            linkListener.get(LinkType.PARAM, param, false);
        if (next != null)
            linkListener.get(LinkType.NEXT, next, false);
        if (prop != null)
            linkListener.get(LinkType.PROP, prop, false);
        if (cell != null)
            linkListener.get(LinkType.CELL, cell, false);
    }

    @Override
    public void parse(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        while (bb.hasRemaining()) {
            LinkType linkType = LinkType.values()[bb.get()];
            LinkDataType linkDataType = LinkDataType.values()[bb.get()];
            Object linkData = null;
            switch (linkDataType) {
                case NODE:
                    linkData = bb.getLong();
                    break;
                case BOOLEAN_FALSE:
                    linkData = new BooleanData(false);
                    break;
                case BOOLEAN_TRUE:
                    linkData = new BooleanData(true);
                    break;
                case NUMBER:
                    linkData = new NumberData(bb.getDouble());
                    break;
                case NATIVE_FUNCTION_PATH:
                case STRING:
                    byte[] stringData = new byte[bb.getInt()/*length*/];
                    bb.get(stringData);
                    linkData = new StringData(stringData);
                    break;
                case FILE:
                    linkData = new FileData(storage, bb.getInt());
                    break;
            }
            switch (linkType) {
                case NATIVE_FUNCTION:
                    function = FunctionManager.functions.get(new String(((StringData)linkData).bytes));
                    break;
                case VALUE:
                    value = linkData;
                    break;
                case SOURCE:
                    source = linkData;
                    break;
                case TITLE:
                    title = (StringData) linkData;
                    break;
                case SET:
                    set = linkData;
                    break;
                case TRUE:
                    _true = linkData;
                    break;
                case ELSE:
                    _else = linkData;
                    break;
                case EXIT:
                    exit = linkData;
                    break;
                case WHILE:
                    _while = linkData;
                    break;
                case IF:
                    _if = linkData;
                    break;
                case PROTOTYPE:
                    prototype = linkData;
                    break;
                case LOCAL_PARENT:
                    localParent = linkData;
                    break;
                case PARSER:
                    parser = (StringData) linkData;
                    break;
                case LOCAL:
                    if (local == null)
                        local = new ArrayList<>();
                    local.add(linkData);
                    break;
                case PARAM:
                    if (param == null)
                        param = new ArrayList<>();
                    param.add(linkData);
                    break;
                case NEXT:
                    if (next == null)
                        next = new ArrayList<>();
                    next.add(linkData);
                    break;
                case CELL:
                    if (cell == null)
                        cell = new ArrayList<>();
                    cell.add(linkData);
            }
        }
    }

}
