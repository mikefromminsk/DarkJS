package com.metabrain.gdb;

import com.metabrain.djs.node.Node;
import com.metabrain.djs.node.NodeBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeBuilderTest {

    static void setValue(){
        NodeBuilder builder = new NodeBuilder();
        Long valueNodeId = builder.create().getId();
        Node nodeId = builder.create().setValue(valueNodeId).commit();
        Long valueId = builder.set(nodeId).getValue();
        assertEquals(valueNodeId, valueId);
    }

    public static void main(String[] args) {
        System.out.println(1);
        setValue();
    }

}
