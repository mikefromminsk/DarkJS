package org.pdk.storage.model.node.link;

public interface NodeLinkListener {
    void get(LinkType linkType, Object link, boolean singleValue);
}
