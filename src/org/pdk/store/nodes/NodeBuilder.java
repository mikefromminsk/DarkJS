package org.pdk.store.nodes;

import org.pdk.store.Storage;
import org.pdk.files.Files;
import org.pdk.store.LinkType;
import org.pdk.store.NodeType;
import org.pdk.convertors.node.NodeSerializer;
import org.pdk.instance.Instance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class NodeBuilder {

    private Storage storage = Instance.get().getStorage();
    private Node node;

    public NodeBuilder create() {
        return create(NodeType.NODE);
    }

    public NodeBuilder create(NodeType nodeType) {
        node = storage.newNode(nodeType);
        return this;
    }

    public Node createString(String string) {
        return create(NodeType.STRING).setData(string).commit();
    }

    public Node createNumber(Double number) {
        return create(NodeType.NUMBER).setData(number).commit();
    }

    public Node createBool(Boolean bool) {
        return create(NodeType.BOOLEAN).setData(bool).commit();
    }

    public NodeBuilder get(Long id) {
        node = storage.get(id);
        return this;
    }

    public NodeBuilder set(Node node) {
        this.node = node;
        return this;
    }

    public Node commit() {
        if (node.type.ordinal() >= NodeType.NODE.ordinal()) {
            if (node.id == null)
                storage.add(node);
            else
                storage.set(node.id, node);
            if (!node.isSaved)
                storage.addToTransaction(node);
        } else {
            Data data = (Data) node;
            storage.add(data);
            if (data.data == null)
                node = null;
        }
        return node;
    }

    public Long getId() {
        if (node.id == null)
            commit();
        return node.id;
    }

    // This function should use because node type create only by NodeBuilder.create
    //public Long setType() { }

    public DataInputStream getData() {
        return ((Data) node).data;
    }

    public NodeBuilder setData(Boolean bool) {
        setData(bool ? NodeSerializer.TRUE : NodeSerializer.FALSE);
        return this;
    }

    public NodeBuilder setData(Double number) {
        setData(number.toString());
        return this;
    }

    public NodeBuilder setData(String string) {
        setData(string.getBytes());
        return this;
    }

    public NodeBuilder setData(byte[] data) {
        setData(new ByteArrayInputStream(data));
        return this;
    }

    public NodeBuilder setData(InputStream stream) {
        ((Data) node).externalData = stream;
        return this;
    }

    public Long getValue() {
        if (node.value instanceof Long)
            return (Long) node.value;
        else if (node.value instanceof Node)
            return node.id;
        return null;
    }

    public Long getSource() {
        if (node.source instanceof Long)
            return (Long) node.source;
        else if (node.source instanceof Node)
            return node.id;
        return null;
    }

    public Long getTitle() {
        if (node.title instanceof Long)
            return (Long) node.title;
        else if (node.title instanceof Node)
            return node.id;
        return null;
    }

    public Long getSet() {
        if (node.set instanceof Long)
            return (Long) node.set;
        else if (node.set instanceof Node)
            return node.id;
        return null;
    }

    public Long getTrue() {
        if (node._true instanceof Long)
            return (Long) node._true;
        else if (node._true instanceof Node)
            return node.id;
        return null;
    }

    public Long getElse() {
        if (node._else instanceof Long)
            return (Long) node._else;
        else if (node._else instanceof Node)
            return node.id;
        return null;
    }

    public Long getExit() {
        if (node.exit instanceof Long)
            return (Long) node.exit;
        else if (node.exit instanceof Node)
            return node.id;
        return null;
    }

    public Long getWhile() {
        if (node._while instanceof Long)
            return (Long) node._while;
        else if (node._while instanceof Node)
            return node.id;
        return null;
    }

    public Long getIf() {
        if (node._if instanceof Long)
            return (Long) node._if;
        else if (node._if instanceof Node)
            return node.id;
        return null;
    }

    public Long getPrototype() {
        if (node.prototype instanceof Long)
            return (Long) node.prototype;
        else if (node.prototype instanceof Node)
            return node.id;
        return null;
    }

    public Long getLocalParent() {
        if (node.localParent instanceof Long)
            return (Long) node.localParent;
        else if (node.localParent instanceof Node)
            return node.id;
        return null;
    }

    public Node getValueNode() {
        if (node.value instanceof Node)
            return (Node) node.value;
        else if (node.value instanceof Long)
            return (Node) (node.value = storage.get((Long) node.value));
        return null;
    }

    public Node getSourceNode() {
        if (node.source instanceof Node)
            return (Node) node.source;
        else if (node.source instanceof Long)
            return (Node) (node.source = storage.get((Long) node.source));
        return null;
    }

    public Data getTitleNode() {
        if (node.title instanceof Data)
            return (Data) node.title;
        else if (node.title instanceof Long)
            return (Data) (node.title = storage.get((Long) node.title));
        return null;
    }

    public Node getSetNode() {
        if (node.set instanceof Node)
            return (Node) node.set;
        else if (node.set instanceof Long)
            return (Node) (node.set = storage.get((Long) node.set));
        return null;
    }

    public Node getTrueNode() {
        if (node._true instanceof Node)
            return (Node) node._true;
        else if (node._true instanceof Long)
            return (Node) (node._true = storage.get((Long) node._true));
        return null;
    }

    public Node getElseNode() {
        if (node._else instanceof Node)
            return (Node) node._else;
        else if (node._else instanceof Long)
            return (Node) (node._else = storage.get((Long) node._else));
        return null;
    }

    public Node getExitNode() {
        if (node.exit instanceof Node)
            return (Node) node.exit;
        else if (node.exit instanceof Long)
            return (Node) (node.exit = storage.get((Long) node.exit));
        return null;
    }

    public Node getWhileNode() {
        if (node._while instanceof Node)
            return (Node) node._while;
        else if (node._while instanceof Long)
            return (Node) (node._while = storage.get((Long) node._while));
        return null;
    }

    public Node getIfNode() {
        if (node._if instanceof Node)
            return (Node) node._if;
        else if (node._if instanceof Long)
            return (Node) (node._if = storage.get((Long) node._if));
        return null;
    }

    public Node getPrototypeNode() {
        if (node.prototype instanceof Node)
            return (Node) node.prototype;
        else if (node.prototype instanceof Long)
            return (Node) (node.prototype = storage.get((Long) node.prototype));
        return null;
    }

    public Node getLocalParentNode() {
        if (node.localParent instanceof Node)
            return (Node) node.localParent;
        else if (node.localParent instanceof Long)
            return (Node) (node.localParent = storage.get((Long) node.localParent));
        return null;
    }

    public Data getParserNode() {
        if (node.parser instanceof Node)
            return (Data) node.parser;
        else if (node.parser instanceof Long)
            return (Data) (node.parser = storage.get((Long) node.parser));
        return null;
    }

    public NodeBuilder setValue(Node value) {
        node.value = value;
        return this;
    }

    public NodeBuilder setSource(Node source) {
        node.source = source;
        return this;
    }

    public NodeBuilder setTitle(Node title) {
        node.title = title;
        return this;
    }

    public NodeBuilder setSet(Node set) {
        node.set = set;
        return this;
    }

    public NodeBuilder setTrue(Node _true) {
        node._true = _true;
        return this;
    }

    public NodeBuilder setElse(Node _else) {
        node._else = _else;
        return this;
    }

    public NodeBuilder setExit(Node exit) {
        node.exit = exit;
        return this;
    }

    public NodeBuilder setWhile(Node _while) {
        node._while = _while;
        return this;
    }

    public NodeBuilder setIf(Node _if) {
        node._if = _if;
        return this;
    }

    public NodeBuilder setPrototype(Node prototype) {
        node.prototype = prototype;
        return this;
    }

    public NodeBuilder setLocalParent(Node localParent) {
        node.localParent = localParent;
        return this;
    }

    public NodeBuilder setParser(Node parser) {
        node.parser = parser;
        return this;
    }

    public NodeBuilder setParser(String parser) {
        Node parserNode = null;
        if (parser != null) {
            Node prevNode = node;
            parserNode = createString(parser.toLowerCase());
            node = prevNode;
        }
        node.parser = parserNode;
        return this;
    }

    private int linksCount(ArrayList links) {
        return links != null ? links.size() : 0;
    }

    public int getLocalCount() {
        return linksCount(node.local);
    }

    public int getParamCount() {
        return linksCount(node.param);
    }

    public int getNextCount() {
        return linksCount(node.next);
    }

    public int getCellCount() {
        return linksCount(node.cell);
    }

    public int getPropertiesCount() {
        return linksCount(node.prop);
    }

    private Node getListNode(ArrayList<Object> list, int index) {
        if (list != null && index >= 0 && index < list.size()) {
            Object object = list.get(index);
            if (object instanceof Node) return (Node) object;
            if (object instanceof Long) {
                Node readedNode = storage.get((Long) object);
                list.set(index, readedNode);
                return readedNode;
            }
        }
        return null;
    }

    public Node getLocalNode(int index) {
        return getListNode(node.local, index);
    }

    public Node getParamNode(int index) {
        return getListNode(node.param, index);
    }

    public Node getNextNode(int index) {
        return getListNode(node.next, index);
    }

    public Node getCellNode(int index) {
        return getListNode(node.cell, index);
    }

    public Node getPropertyNode(int index) {
        return getListNode(node.prop, index);
    }

    public NodeBuilder addLocal(Long id) {
        if (node.local == null)
            node.local = new ArrayList<>();
        node.local.add(id);
        return this;
    }

    public NodeBuilder addParam(Long id) {
        if (node.param == null)
            node.param = new ArrayList<>();
        node.param.add(id);
        return this;
    }

    public NodeBuilder addNext(Long id) {
        if (node.next == null)
            node.next = new ArrayList<>();
        node.next.add(id);
        return this;
    }

    public NodeBuilder addCell(Long id) {
        if (node.cell == null)
            node.cell = new ArrayList<>();
        node.cell.add(id);
        return this;
    }

    public NodeBuilder addProperty(Long id) {
        if (node.prop == null)
            node.prop = new ArrayList<>();
        node.prop.add(id);
        return this;
    }

    public NodeBuilder addLocal(Node item) {
        if (node.local == null)
            node.local = new ArrayList<>();
        node.local.add(item);
        Node prevNode = node;
        set(item);
        setLocalParent(prevNode);
        commit();
        set(prevNode);
        return this;
    }

    public NodeBuilder addParam(Node item) {
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

    public NodeBuilder addCell(Node item) {
        if (node.cell == null)
            node.cell = new ArrayList<>();
        node.cell.add(item);
        return this;
    }

    public NodeBuilder addProperty(Node item) {
        if (node.prop == null)
            node.prop = new ArrayList<>();
        node.prop.add(item);
        return this;
    }

    public void removeFromListNode(ArrayList<Object> list, Long nodeId) {
        if (list != null && nodeId != null) {
            Object find = null;
            for (Object item : list) {
                if (item instanceof Long && nodeId.equals(item)
                        || item instanceof Node && nodeId.equals(((Node) item).id)) {
                    find = item;
                    break;
                }
            }
            if (find != null)
                list.remove(find);
        }
    }

    public NodeBuilder removeLocal(Long nodeId) {
        removeFromListNode(node.local, nodeId);
        return this;
    }

    public NodeBuilder removeParam(Long nodeId) {
        removeFromListNode(node.param, nodeId);
        return this;
    }

    public NodeBuilder removeNext(Long nodeId) {
        removeFromListNode(node.next, nodeId);
        return this;
    }

    public NodeBuilder removeCell(Long nodeId) {
        removeFromListNode(node.cell, nodeId);
        return this;
    }

    public NodeBuilder removeProperty(Long nodeId) {
        removeFromListNode(node.prop, nodeId);
        return this;
    }

    public NodeBuilder removeLocal(Node item) {
        if (item != null) removeFromListNode(node.local, item.id);
        return this;
    }

    public NodeBuilder removeParam(Node item) {
        if (item != null) removeFromListNode(node.local, item.id);
        return this;
    }

    public NodeBuilder removeNext(Node item) {
        if (item != null) removeFromListNode(node.local, item.id);
        return this;
    }

    public NodeBuilder removeCell(Node item) {
        if (item != null) removeFromListNode(node.local, item.id);
        return this;
    }

    public NodeBuilder removeProperty(Node item) {
        if (item != null) removeFromListNode(node.local, item.id);
        return this;
    }

    public NodeBuilder setFunctionIndex(int functionIndex) {
        ((NativeNode) node).setFunctionIndex(functionIndex);
        return this;
    }

    public Node findLocal(String title) {
        if (node.id == null)
            commit();
        //TODO reomve Files.getNodeIfExist
        return Files.getNodeIfExist(node, title);
    }

    public Node findParam(String findTitle) {
        if (node.id == null)
            commit();
        Node[] params = getParams();
        Node prev = node;
        Node findNode = null;
        for (Node param : params) {
            node = param;
            if (findTitle.equals(getTitleString())) {
                findNode = node;
                break;
            }
        }
        node = prev;
        return findNode;
    }


    public Node getValueOrSelf() {
        Node value = getValueNode();
        return value != null ? value : node;
    }

    public Node getNode() {
        return node;
    }

    public NodeBuilder setLink(LinkType linkType, Node linkValueNode) {
        switch (linkType) {
            case VALUE:
                setValue(linkValueNode);
                break;
            case SOURCE:
                setSource(linkValueNode);
                break;
            case TITLE:
                setTitle(linkValueNode);
                break;
            case SET:
                setSet(linkValueNode);
                break;
            case TRUE:
                setTrue(linkValueNode);
                break;
            case ELSE:
                setElse(linkValueNode);
                break;
            case EXIT:
                setExit(linkValueNode);
                break;
            case WHILE:
                setWhile(linkValueNode);
                break;
            case IF:
                setIf(linkValueNode);
                break;
            case PROP:
                addProperty(linkValueNode);
                break;
            case PROTOTYPE:
                setPrototype(linkValueNode);
                break;
            case LOCAL_PARENT:
                setLocalParent(linkValueNode);
                break;
            case PARSER:
                setParser(linkValueNode);
                break;
            case LOCAL:
                addLocal(linkValueNode);
                break;
            case PARAM:
                addParam(linkValueNode);
                break;
            case NEXT:
                addNext(linkValueNode);
                break;
            case CELL:
                addCell(linkValueNode);
                break;
        }
        return this;
    }

    public NodeBuilder removeAllNext() {
        node.next = null;
        return this;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        storage.transactionCommit();
    }

    public NodeType getType() {
        return node.type;
    }

    public boolean isData() {
        return node.type.ordinal() < NodeType.NODE.ordinal();
    }

    public boolean isDataVariable() {
        return getValueNode() != null && getValueNode().type.ordinal() < NodeType.NODE.ordinal();
    }

    public boolean isString() {
        return node.type == NodeType.STRING;
    }

    public boolean isNumber() {
        return node.type == NodeType.NUMBER;
    }

    public boolean isBoolean() {
        return node.type == NodeType.BOOLEAN;
    }

    public boolean isVar() {
        return node.type == NodeType.NODE;
    }

    public boolean isFunction() {
        return getNextCount() > 0 || isNativeFunction();
    }

    public boolean isArray() {
        return getCellCount() > 0;
    }

    public boolean isObject() {
        return getLocalCount() > 0 && !isFunction();
    }

    public boolean isNativeFunction() {
        return node.type == NodeType.NATIVE_FUNCTION;
    }

    public boolean isThread() {
        return node.type == NodeType.THREAD;
    }

    public String getTitleString() {
        Data title = getTitleNode();
        if (title != null && title.data != null)
            return title.data.readString();
        return null;
    }

    public String getParserString() {
        Data parser = getParserNode();
        if (parser != null && parser.data != null)
            return parser.data.readString();
        return null;
    }

    public Node[] getLocalNodes() {
        Node[] nodes = new Node[getLocalCount()];
        for (int i = 0; i < getLocalCount(); i++)
            nodes[i] = getLocalNode(i);
        return nodes;
    }

    public Node[] getParams() {
        Node[] nodes = new Node[getParamCount()];
        for (int i = 0; i < getParamCount(); i++)
            nodes[i] = getParamNode(i);
        return nodes;
    }

    public Node[] getCells() {
        Node[] nodes = new Node[getCellCount()];
        for (int i = 0; i < getCellCount(); i++)
            nodes[i] = getCellNode(i);
        return nodes;
    }

    public NodeBuilder setLocalNode(int index, Node item) {
        node.local.set(index, item);
        Node prevNode = node;
        set(item);
        setLocalParent(prevNode);
        commit();
        set(prevNode);
        return this;
    }

    public void clearCells() {
        node.cell = null;
    }

    public void setOwnerAccessCode(Long access_token) {
        if (node instanceof ThreadNode) {
            ThreadNode threadNode = (ThreadNode) node;
            threadNode.access_owner = access_token;
        }
    }

    public Integer getFunctionId() {
        return ((NativeNode) node).getFunctionIndex();
    }
}
