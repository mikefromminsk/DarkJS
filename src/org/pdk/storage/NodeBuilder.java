package org.pdk.storage;

import org.pdk.modules.Func;
import org.pdk.storage.model.DataOrNode;
import org.pdk.storage.model.data.Data;
import org.pdk.storage.model.data.NumberData;
import org.pdk.storage.model.data.StringData;
import org.pdk.storage.model.node.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public NodeBuilder setTitle(byte[] paramName) {
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

    public Data getParamData(int index) {
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
        return (StringData) getParamData(index);
    }

    public NumberData getNumberParam(int index) {
        return (NumberData) getParamData(index);
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

    public NodeBuilder setParser(byte[] bytes) {
        if (bytes != null)
            node.parser = new StringData(bytes);
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
        return getNodeFromRoot("master");
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

    public Node getPrototype() {
        if (node.prototype instanceof Node)
            return (Node) node.prototype;
        else if (node.prototype instanceof Long)
            return (Node) (node.prototype = storage.get((Long) node.prototype));
        return null;
    }

    public Node[] getNextList() {
        return getNodes(node.next);
    }

    public NodeBuilder addProperty(DataOrNode item) {
        if (node.prop == null)
            node.prop = new ArrayList<>();
        node.prop.add(item);
        return this;
    }

    public DataOrNode[] getProps() {
        return getDons(node.prop);
    }


    public Node getNode(Node root, byte[][] names, byte[] parser, boolean createIfNotExist) {
        node = root;

        for (int i = 0; i < names.length; i++) {
            byte[] name = names[i];

            boolean find = false;
            if (node.local != null) {
                for (Node local : getLocals()) {
                    if (Arrays.equals(name, local.title.bytes)) {
                        node = local;
                        find = true;
                        break;
                    }
                }
            }

            if (!find) {
                if (createIfNotExist) {
                    Node prev = node;
                    Node newNode = create().setTitle(name).commit();
                    if (i == names.length - 1)
                        setParser(parser);
                    set(prev).addLocal(newNode).commit();
                    node = newNode;
                } else {
                    return null;
                }
            }
        }

        return node;
    }

    // TODO create getNode with creator first
    public Node getNode(Node root, String path, boolean createIfNotExist) {

        // TODO add escape characters /

        if (path != null && !path.equals("")) {
            List<String> list = Arrays.asList(path.split("/"));
            list.removeAll(Arrays.asList("", null));

            byte[][] names = new byte[list.size()][];

            byte[] parser = null;
            String lastName = list.get(list.size() - 1);
            if (lastName.contains(".")) {
                int dotPos = lastName.indexOf('.');
                parser = lastName.substring(dotPos + 1).getBytes();
            }

            for (int i = 0; i < list.size(); i++) {
                String item = list.get(i);
                int dotIndex = item.indexOf('.');
                names[i] = (dotIndex != -1) ? item.substring(0, dotIndex).getBytes() : item.getBytes();
            }

            return getNode(root, names, parser, createIfNotExist);
        }
        return root;
    }

    public Node getNode(Node root, byte[] name) {
        return getNode(root, new byte[][]{name}, null, true);
    }

    public Node getNode(Node root, String path) {
        return getNode(root, path, true);
    }

    public Node getNode(String path) {
        return getNode(node, path, true);
    }

    public Node getNodeIfExist(Node root, String path) {
        return getNode(root, path, false);
    }

    public Node getNodeIfExist(Node root, byte[] name) {
        return getNode(root, new byte[][]{name}, null, false);
    }

    public Node getNodeIfExist(String path) {
        return getNode(getMaster(), path, false);
    }

    public Node getNodeFromRoot(String path, boolean createIfNotExist) {
        return getNode(getRoot(), path, createIfNotExist);
    }

    public Node getNodeFromRoot(String path) {
        return getNode(getRoot(), path, true);
    }

    public Node getNodeFromRootIfExist(String path) {
        return getNode(getRoot(), path, false);
    }

    public NodeBuilder setPrototype(Node item) {
        node.prototype = item;
        return this;
    }
}
