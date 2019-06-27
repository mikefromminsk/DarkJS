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
        if (functionId != null)
            linkListener.get(LinkType.NATIVE_FUNCTION_NUMBER, (long) (int) functionId, true);
    }


    @Override
    void restore(LinkType linkType, long linkData) {
        super.restore(linkType, linkData);
        if (linkType == LinkType.NATIVE_FUNCTION_NUMBER)
            functionId = (int) linkData;
    }
}
