package org.pdk.store.model.node;

import org.pdk.store.model.DataOrNode;
import org.pdk.store.model.data.*;
import org.pdk.store.model.node.link.Link;
import org.pdk.store.model.node.link.LinkDataType;
import org.pdk.store.model.node.link.LinkType;
import org.simpledb.InfinityStringArrayCell;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Node implements InfinityStringArrayCell, DataOrNode {

    public boolean isSaved;
    public Long nodeId;
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

    @Override
    public byte[] build() {
        ArrayList<Link> links = new ArrayList<>();
        listLinks((linkType, link, singleValue) -> {
            Link newLink = new Link();
            newLink.linkType = linkType;
            if (link instanceof Integer){
                newLink.linkDataType = LinkDataType.NUMBER; // for NativeNode.functionId
                newLink.linkData.putLong((long)link);
            } else if (link instanceof Long) {
                newLink.linkDataType = LinkDataType.NODE;
                newLink.linkData.putLong((Long) link);
            } else if (link instanceof Data) {
                if (link instanceof BooleanData) {
                    newLink.linkDataType = LinkDataType.BOOL;
                    newLink.linkData.putLong(((BooleanData) link).bool ? 1L : 0L);
                } else if (link instanceof NumberData) {
                    newLink.linkDataType = LinkDataType.NUMBER;
                    newLink.linkData.putDouble(((NumberData) link).number);
                } else if (link instanceof StringData) {
                    newLink.linkDataType = LinkDataType.STRING;
                    newLink.linkData.putLong(((StringData) link).stringId);
                } else if (link instanceof FileData){
                    newLink.linkDataType = LinkDataType.FILE;
                    newLink.linkData.putLong(((FileData) link).fileId);
                }
            } else if (link instanceof Node){
                newLink.linkDataType = LinkDataType.NODE;
                newLink.linkData.putLong(((Node) link).nodeId);
            }
            links.add(newLink);
        });
        ByteBuffer bb = ByteBuffer.allocate(links.size() * Link.SIZE);
        for (Link link: links)
            bb.put(link.build());
        return bb.array();
    }

    public interface NodeLinkListener {
        void get(LinkType linkType, Object link, boolean singleValue);
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
        for (int i = 0; i < data.length / Link.SIZE; i++) {
            Link link = new Link();
            link.parse(Arrays.copyOfRange(data, i * Link.SIZE, i * (Link.SIZE + 1) - 1));
            Object restoredLink = null;
            switch (link.linkDataType) {
                case BOOL:
                    restoredLink = new BooleanData(link.linkData.getInt() == 1);
                    break;
                case NUMBER:
                    restoredLink = new NumberData(link.linkData.getDouble());
                    break;
                case STRING:
                    restoredLink = new StringData(link.linkData.getLong());
                    break;
                case FILE:
                    restoredLink = new FileData(link.linkData.getLong());
                    break;
                case NODE:
                    restoredLink = link.linkData.getLong();
                    break;
            }
            restore(link.linkType, restoredLink);
        }
    }

    public void restore(LinkType linkType, Object linkData) {
        switch (linkType) {
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
