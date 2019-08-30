package com.droid.djs.nodes;

import com.droid.djs.nodes.consts.LinkType;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.gdb.Bytes;

import java.util.ArrayList;

public class Node extends SuperNode {

    public boolean isSaved;
    public Long id;
    // TODO move type to node body in the storage
    // TODO add flag isData into nodeMeta
    public NodeType type;
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
    // TODO add sourceParent for GC and Files.replace

    public ArrayList<Object> local; // TODO rename to locals
    public ArrayList<Object> param; // TODO rename to params
    public ArrayList<Object> next; // TODO merge next and cell
    public ArrayList<Object> cell;
    public ArrayList<Object> prop;
    // after addObject new link you should addObject it to listLinks and build function

    public Node(NodeType type) {
        this.type = type;
    }

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
    void restore(LinkType linkType, long linkData) {
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
