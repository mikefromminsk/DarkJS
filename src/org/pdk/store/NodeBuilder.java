package org.pdk.store;

import org.pdk.store.model.DataOrNode;
import org.pdk.store.model.data.Data;
import org.pdk.store.model.data.NumberData;
import org.pdk.store.model.data.StringData;
import org.pdk.store.model.node.Node;
import org.pdk.store.model.node.meta.NodeType;

import java.util.ArrayList;

public class NodeBuilder {

    private Storage storage;
    private Node node;

    public NodeBuilder(Storage storage) {
        this.storage = storage;
    }

    public NodeBuilder create() {
        return create(NodeType.NODE);
    }

    public NodeBuilder create(NodeType nodeType) {
        storage.createNodeInstance(nodeType);
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

    public NodeBuilder setTitle(String paramName) {
        node.title = new StringData(paramName);
        return this;
    }

    public DataOrNode getValue() {
        return (DataOrNode) node.value;
    }

    public DataOrNode getParam(int index) {
        return (DataOrNode) node.param.get(index);
    }

    public Node getNode() {
        return node;
    }

    public Data getDataParam(int index) {
        Node node = getNode();
        Node param = (Node) getParam(index);
        Data paramValue = (Data) set(param).getValue();
        set(node);
        return paramValue;
    }

    public StringData getStringParam(int index) {
        return (StringData) getDataParam(index);
    }

    public NumberData getNumberParam(int index) {
        return (NumberData) getDataParam(index);
    }

    public String getTitle() {
        return new String(node.title.bytes);
    }

    public String getParser() {
        return new String(node.parser.bytes);
    }

    public Node getLocalParent() {
        return (Node) node.localParent;
    }

    public Node[] getLocals() {
        if (node == null) return null;
        return getNodes(node.local);
    }

    private Node[] getNodes(ArrayList<Object> list) {
        if (list == null) return null;
        Node[] nodes = new Node[list.size()];
        for (int i = 0; i < list.size(); i++){
            Object object = list.get(i);
            if (object instanceof Long)
                list.set(i, storage.get((Long) object));
            nodes[i] = (Node) list.get(i);
        }
        return nodes;
    }

    private DataOrNode[] getDons(ArrayList<Object> list) {
        if (list == null) return null;
        DataOrNode[] dons = new DataOrNode[list.size()];
        for (int i = 0; i < list.size(); i++){
            Object object = list.get(i);
            if (object instanceof Long)
                list.set(i, storage.get((Long) object));
            dons[i] = (DataOrNode) list.get(i);
        }
        return dons;
    }

    public Node commit() {
        if (node.nodeId == null)
            storage.add(node);
        else
            storage.set(node.nodeId, node);
        if (!node.isSaved)
            storage.addToTransaction(node);
        return node;
    }

    public NodeBuilder setParser(String string){
        if (string == null){

        }
    }

    public NodeBuilder addLocal(DataOrNode don){

    }

    public NodeBuilder setLocal(int index, DataOrNode don){
        node.local.set(index, don);
        return this;
    }

    public NodeBuilder removeLocal(DataOrNode don) {
        node.local.remove(don);
        return this;
    }

    public Node getMaster() {
        return null;
    }
}
