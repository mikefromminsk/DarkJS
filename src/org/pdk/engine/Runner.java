package org.pdk.engine;


import org.pdk.modules.ModuleManager;
import org.pdk.storage.NodeBuilder;
import org.pdk.storage.model.DataOrNode;
import org.pdk.storage.model.data.BooleanData;
import org.pdk.storage.model.data.Data;
import org.pdk.storage.model.data.NumberData;
import org.pdk.storage.model.data.StringData;
import org.pdk.storage.model.node.Node;

import java.util.Arrays;

public class Runner {

    private NodeBuilder builder;
    private ModuleManager moduleManager;

    public Runner(NodeBuilder builder, ModuleManager moduleManager) {
        this.builder = builder;
        this.moduleManager = moduleManager;
    }


    private Node getNodePrototype(DataOrNode nodeValue) {
        if (nodeValue instanceof StringData) {
            return moduleManager.stringPrototype.node;
        }
        return null;
    }

    private Node propCalledNode = null;

    private Node getProps(Node node) {

        Node startNode = node;
        if (startNode.source != null && startNode.prop != null)
            node = builder.set(startNode).getSource();

        boolean startFromThis = startNode.source == null && node.prop != null;
        if (startFromThis && propCalledNode != null)
            startNode = propCalledNode;

        if (startNode.prop != null)
            propCalledNode = node;

        if (startNode.prop != null)
            for (DataOrNode propNode : builder.set(startNode).getProps()) {
                DataOrNode propNameNode;
                if (propNode instanceof Node) {
                    run((Node) propNode);
                    propNameNode = builder.set((Node) propNode).getValue();
                } else {
                    propNameNode = propNode;
                }
                if (propNameNode instanceof StringData) {
                    byte[] propName = ((StringData) propNameNode).bytes;

                    DataOrNode nodeValue = builder.set(node).getValue();

                    Node prototypeNode = builder.set(node).getPrototype();
                    if (Arrays.equals("prototype".getBytes(), propName)) {
                        if (prototypeNode != null) {
                            node = prototypeNode;
                        } else {// proto by node type
                            if (nodeValue != null)
                                prototypeNode = getNodePrototype(nodeValue);
                            if (prototypeNode != null) {
                                node = prototypeNode;
                            } else { // create proto
                                prototypeNode = builder.create().commit();
                                builder.set(node).setPrototype(prototypeNode).commit();
                                node = prototypeNode;
                            }
                        }
                        continue;
                    } else { // otherwise
                        Node findPropNode = builder.getNodeIfExist(node, propName);
                        while (prototypeNode != null && findPropNode == null) {
                            findPropNode = builder.getNodeIfExist(prototypeNode, propName);
                            if (findPropNode == null)
                                prototypeNode = builder.set(prototypeNode).getPrototype();
                        }
                        if (findPropNode != null) {
                            propCalledNode = node;
                            node = findPropNode;
                            continue;
                        } else {
                            if (nodeValue != null)
                                prototypeNode = getNodePrototype(nodeValue);
                            if (prototypeNode != null) {
                                findPropNode = builder.getNodeIfExist(prototypeNode, propName);
                                if (findPropNode != null) {
                                    node = findPropNode;
                                    continue;
                                }
                            }
                        }
                    }
                    propCalledNode = node;
                    Node newField = builder.create().setTitle(propName).commit();
                    builder.set(node).addLocal(newField).commit();
                    node = newField;
                } else if (propNameNode instanceof NumberData) {
                    /*int index = (int) builder.set(propNameNode).getData().getObject();
                    propCalledNode = node;
                    node = builder.set(node).getCellNode(index);*/
                }
            }
        return node;
    }

    public void run(Node node) {
        run(node, null);
    }

    private Node exitNode = null;

    public void run(Node node, Node calledNodeId) {

        if (node.func != null) {
            // TODO duplicate params
            if (node.param != null)
                for (DataOrNode param : builder.set(node).getParams())
                    if (param instanceof Node)
                        run((Node) param, calledNodeId);
            DataOrNode result = node.func.invoke(builder.set(node), calledNodeId);
            builder.set(node).setValue(result).commit();
        } else {

            if (node.next != null)
                for (Node next : builder.set(node).getNextList()) {
                    run(next);
                    if (exitNode != null) {
                        if (exitNode.equals(node))
                            exitNode = null;
                        break;
                    }
                }

            if (node.source != null) {

                propCalledNode = calledNodeId;
                Node source = getProps(builder.set(node).getSource());
                Node calledObjectFromSource = propCalledNode;

                DataOrNode set = builder.set(node).getSet();
                if (set != null) {
                    if (set instanceof Node) {
                        Node setNode = (Node) set;
                        run(setNode, calledObjectFromSource);
                        DataOrNode setValue = builder.set(setNode).getValue();
                        builder.set(source).setValue(setValue).commit();
                    } else if (set instanceof Data) {
                        builder.set(source).setValue(set).commit();
                    }
                } else {
                    if (calledObjectFromSource.func != null) {
                        run(source, calledObjectFromSource);
                    } else {
                        if (node.param != null) {
                            DataOrNode[] sourceParams = builder.set(source).getParams();
                            DataOrNode[] nodeParams = builder.set(node).getParams();
                            for (int i = 0; i < nodeParams.length; i++) {
                                DataOrNode sourceParam = nodeParams[i];
                                Data sourceParamData = null;
                                if (sourceParam instanceof Node) {
                                    Node sourceParamNode = (Node) sourceParam;
                                    run(sourceParamNode, calledObjectFromSource);
                                    sourceParamData = (Data) builder.set(sourceParamNode).getValue();
                                } else if (sourceParam instanceof Data) {
                                    sourceParamData = (Data) sourceParam;
                                }
                                builder.set((Node) sourceParams[i]).setValue(sourceParamData).commit();
                            }
                        }
                        run(source, calledObjectFromSource);
                        DataOrNode sourceValue = builder.set(source).getValue();
                        builder.set(node).setValue(sourceValue).commit();
                    }
                }
            }

            if (node._if != null && node._true != null) {
                Node ifNode = builder.set(node).getIf();
                run(ifNode, calledNodeId);
                Object ifNodeData = builder.set(ifNode).getValue();
                if (ifNodeData instanceof BooleanData && ((BooleanData) ifNodeData).value)
                    run(builder.set(node).getTrue(), calledNodeId);
                else if (node._else != null)
                    run(builder.set(node).getElse(), calledNodeId);
            }


            if (node._while != null && node._if != null) {
                Node ifNode = builder.set(node).getIf();
                run(ifNode, calledNodeId);
                BooleanData ifValue = (BooleanData) builder.set(ifNode).getValue();
                Node whileNode = builder.set(node).getWhile();
                while (ifValue.value) {
                    run(whileNode, calledNodeId);
                    if (exitNode != null) {
                        if (exitNode.equals(node))
                            exitNode = null;
                        break;
                    }
                    run(ifNode, calledNodeId);
                    ifValue = (BooleanData) builder.set(ifNode).getValue();
                }
            }

            if (node.exit != null)
                exitNode = builder.set(node).getExit();
        }

    }

}
