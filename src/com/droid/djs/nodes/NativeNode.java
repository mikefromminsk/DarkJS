package com.droid.djs.nodes;

import com.droid.djs.consts.LinkType;
import com.droid.djs.consts.NodeType;

public class NativeNode extends Node {

    public Integer functionId;

    public NativeNode() {
        super(NodeType.NATIVE_FUNCTION);
    }

    @Override
    public void listLinks(NodeLinkListener linkListener) {
        super.listLinks(linkListener);
        linkListener.get(LinkType.SOURCE, functionId, true);
    }


    @Override
    void restore(byte linkType, long linkId) {
        super.restore(linkType, linkId);
        if (linkType == LinkType.VALUE)
            functionId = (int) linkId;
    }
}
