package org.pdk.store;

import org.pdk.files.Files;
import org.pdk.modules.Func;
import org.pdk.store.model.DataOrNode;
import org.pdk.store.model.data.Data;
import org.pdk.store.model.data.NumberData;
import org.pdk.store.model.data.StringData;
import org.pdk.store.model.node.Node;

import java.util.ArrayList;

public class NodeBuilder {

    private Storage storage;
    private Node node;

    public NodeBuilder(Storage storage) {
        this.storage = storage;
    }

    public NodeBuilder create() {
        node = new Node(storage);
        return this;
    }

    public NodeBuilder set(Node node) {
        this.node = node;
        return this;
    }

    public NodeBuilder get(Long id) {
        node = storage.get(id);
        storage.addToCache(node);
        return this;
    }

    public Node commit() {
        if (node.nodeId == null) {
            node.nodeId = storage.newNodeId();
            storage.addToCache(node);
        }
        storage.addToTransaction(node);
        return node;
    }

    public NodeBuilder setTitle(String paramName) {
        node.title = new StringData(paramName.getBytes());
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
        DataOrNode param = getParam(index);
        if (param instanceof Node)
            param = set((Node) param).getValue();
        set(node);
        if (param != null)
            return (Data) param;
        return null;
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
        for (int i = 0; i < list.size(); i++) {
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
        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);
            if (object instanceof Long)
                list.set(i, storage.get((Long) object));
            dons[i] = (DataOrNode) list.get(i);
        }
        return dons;
    }

    public NodeBuilder setParser(String string) {
        if (string != null)
            node.parser = new StringData(string.getBytes());
        return this;
    }

    public NodeBuilder addLocal(Node item) {
        if (node.local == null)
            node.local = new ArrayList<>();
        node.local.add(item);
        Node prevNode = node;
        node = item;
        node.localParent = prevNode;
        commit();
        node = prevNode;
        return this;
    }

    public NodeBuilder setLocal(int index, Node item) {
        node.local.set(index, item);
        Node prevNode = node;
        set(item);
        node.localParent = prevNode;
        commit();
        set(prevNode);
        return this;
    }

    public NodeBuilder removeLocal(Node node) {
        node.local.remove(node);
        return this;
    }

    public Node getMaster() {
        return Files.getNodeFromRoot(this, "master");
    }

    public Node getRoot() {
        return storage.get(0L);
    }

    public NodeBuilder addParam(DataOrNode item) {
        if (node.param == null)
            node.param = new ArrayList<>();
        node.param.add(item);
        return this;
    }

    public NodeBuilder addNext(Node item) {
        if (node.next == null)
            node.next = new ArrayList<>();
        node.next.add(item);
        return this;
    }

    public NodeBuilder setSource(Node item) {
        node.source = item;
        return this;
    }

    public NodeBuilder setSet(DataOrNode item) {
        node.set = item;
        return this;
    }

    public NodeBuilder setWhile(Node item) {
        node._while = item;
        return this;
    }

    public NodeBuilder setIf(Node item) {
        node._if = item;
        return this;
    }

    public NodeBuilder setValue(DataOrNode item) {
        node.value = item;
        return this;
    }

    public NodeBuilder setTrue(Node item) {
        node._true = item;
        return this;
    }

    public NodeBuilder setElse(Node item) {
        node._else = item;
        return this;
    }

    public NodeBuilder setExit(Node item) {
        node.exit = item;
        return this;
    }

    public NodeBuilder setFunc(Func item) {
        node.func = item;
        return this;
    }

    public DataOrNode[] getParams() {
        return getDons(node.param);
    }

    public Node getIf() {
        if (node._if instanceof Node)
            return (Node) node._if;
        else if (node._if instanceof Long)
            return (Node) (node._if = storage.get((Long) node._if));
        return null;
    }

    public Node getTrue() {
        if (node._true instanceof Node)
            return (Node) node._true;
        else if (node._true instanceof Long)
            return (Node) (node._true = storage.get((Long) node._true));
        return null;
    }

    public Node getSource() {
        if (node.source instanceof Node)
            return (Node) node.source;
        else if (node.source instanceof Long)
            return (Node) (node.source = storage.get((Long) node.source));
        return null;
    }

    public DataOrNode getSet() {
        if (node.set instanceof DataOrNode)
            return (DataOrNode) node.set;
        else if (node.set instanceof Long)
            return (Node) (node.set = storage.get((Long) node.set));
        return null;
    }

    public Node getWhile() {
        if (node._while instanceof Node)
            return (Node) node._while;
        else if (node._while instanceof Long)
            return (Node) (node._while = storage.get((Long) node._while));
        return null;
    }

    public Node getElse() {
        if (node._else instanceof Node)
            return (Node) node._else;
        else if (node._else instanceof Long)
            return (Node) (node._else = storage.get((Long) node._else));
        return null;
    }

    public Node getExit() {
        if (node.exit instanceof Node)
            return (Node) node.exit;
        else if (node.exit instanceof Long)
            return (Node) (node.exit = storage.get((Long) node.exit));
        return null;
    }

    public Node[] getNextList() {
        return getNodes(node.next);
    }

}
