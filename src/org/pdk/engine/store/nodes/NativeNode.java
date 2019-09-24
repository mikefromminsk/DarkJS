package org.pdk.engine.store.nodes;

import org.pdk.engine.consts.LinkType;
import org.pdk.engine.consts.NodeType;
import org.pdk.engine.modules.utils.Func;
import org.pdk.instance.Instance;

public class NativeNode extends Node {

    private Integer functionIndex;
    public Func func;

    public NativeNode() {
        super(NodeType.NATIVE_FUNCTION);
    }

    @Override
    public void listLinks(NodeLinkListener linkListener) {
        super.listLinks(linkListener);
        if (functionIndex != null)
            linkListener.get(LinkType.NATIVE_FUNCTION, (long) (int) functionIndex, true);
    }

    public void setFunctionIndex(Integer functionIndex) {
        this.functionIndex = functionIndex;
        func = Instance.get().getFunctions().get(functionIndex);
    }

    public Integer getFunctionIndex(){
        return functionIndex;
    }

    @Override
    void restore(LinkType linkType, long linkData) {
        super.restore(linkType, linkData);
        if (linkType == LinkType.NATIVE_FUNCTION) {
            setFunctionIndex((int) linkData);
        }
    }
}
