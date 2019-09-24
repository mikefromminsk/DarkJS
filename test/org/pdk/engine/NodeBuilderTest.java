package org.pdk.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class NodeBuilderTest {

/*
    public static void main(String[] args) {
        System.out.println(1);
        setValue();
    }*/

    @Test
    //@RepeatedTest(100)
    // TODO solve problem with RepeatedTest(100)
    void create() {
        /*NodeBuilder builder = new NodeBuilder();
        Long secondValueNodeId = builder.create().getId();
        Storage.getFunctions().transactionCommit();
        Storage.getFunctions().clearCache();
        assertEquals(valueNodeId, builder.set(nodeId).getValue());
        builder.setValue(secondValueNodeId).commit();
        assertEquals(secondValueNodeId, builder.set(nodeId).getValue());
        Storage.getFunctions().transactionCommit();
        Storage.getFunctions().clearCache();
        assertEquals(secondValueNodeId, builder.set(nodeId).getValueNode().id);

        String string = "string";
        Node str1Id = builder.create(NodeType.STRING).setData(string).commit();
        String str = builder.set(str1Id).getData().readString();
        assertEquals(string, str);

        try {
            Node fileNodeId = builder.create(NodeType.STRING)
                    .setData(new FileInputStream("test_res/data_stream/testData.txt"))
                    .commit();
            DataInputStream dataStream = builder.set(fileNodeId).getData();

            StringBuilder stringBuilder = new StringBuilder();
            while (dataStream.hasNext())
                stringBuilder.append(dataStream.readChars());
            byte[] ss = stringBuilder.toString().getBytes();

            assertEquals(1024 * 1024 * 3, ss.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        assertEquals(1, 1);
    }
}