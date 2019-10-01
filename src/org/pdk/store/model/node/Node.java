package org.pdk.store.model.node;

import org.pdk.store.consts.LinkType;
import org.pdk.store.model.node.meta.NodeType;
import org.simpledb.Bytes;
import org.simpledb.InfinityStringArrayCell;

import java.util.ArrayList;

public class Node implements InfinityStringArrayCell {

    public boolean isSaved;
    public Long id;
    public Object value;
    public Object source;
    public Object title;
    public Object set;
    public Object _true; 
    public Object _else;
    public Object exit; 
    public Object _while;
    public Object _if;
    public Object prototype;
    public Object localParent;
    public Object parser;

    public ArrayList<Object> local;
    public ArrayList<Object> param;
    public ArrayList<Object> next;
    public ArrayList<Object> cell;
    public ArrayList<Object> prop;
    // after addObject new link you should addObject it to listLinks and build function

    @Override
    public byte[] build() {
        ArrayList<Long> links = new ArrayList<>();
        listLinks((linkType, link, singleValue) -> {
            Long linkId = null;
            if (link instanceof Long)
                linkId = (Long) link;
            else if (link instanceof Node)
                linkId = ((Node) link).id;
            if (linkId == null)
                throw new NullPointerException();
            long dataLink = linkId * 256L + (long) linkType.ordinal();
            links.add(dataLink);
        });
        return Bytes.fromLongList(links);
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
            for (Object item : local)
                linkListener.get(LinkType.LOCAL, item, false);
        if (param != null)
            for (Object item : param)
                linkListener.get(LinkType.PARAM, item, false);
        if (next != null)
            for (Object item : next)
                linkListener.get(LinkType.NEXT, item, false);
        if (prop != null)
            for (Object item : prop)
                linkListener.get(LinkType.PROP, item, false);
        if (cell != null)
            for (Object item : cell)
                linkListener.get(LinkType.CELL, item, false);
    }


    @Override
    public void parse(byte[] data) {
        long[] links = Bytes.toLongArray(data);
        for (long dataLink : links) {
            byte linkType = (byte) (dataLink % 256);
            long linkData = (dataLink - linkType) / 256;
            restore(LinkType.values()[linkType], linkData);
        }
    }

    public void restore(LinkType linkType, long linkData) {
        switch (linkType) {
            case VALUE:
                value = linkData;
                break;
            case SOURCE:
                source = linkData;
                break;
            case TITLE:
                title = linkData;
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
                parser = linkData;
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
