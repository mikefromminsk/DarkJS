package org.pdk.store;

import org.pdk.store.model.node.Node;

public class NodeBuilder {

    private Storage storage;
    private Node node;

    public NodeBuilder(Storage storage) {
        this.storage = storage;
    }

    public NodeBuilder create() {
        node = new Node();
        return this;
    }

    public NodeBuilder get(Long id) {
        node = storage.get(id);
        return this;
    }

    public NodeBuilder set(Node node) {
        this.node = node;
        return this;
    }

}
