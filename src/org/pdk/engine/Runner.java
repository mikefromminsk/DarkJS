package org.pdk.engine;


import org.pdk.store.NodeBuilder;
import org.pdk.store.model.DataOrNode;
import org.pdk.store.model.data.Data;
import org.pdk.store.model.node.Node;

public class Runner {

    private NodeBuilder builder;

    public Runner(NodeBuilder builder) {
        this.builder = builder;
    }

    public void run(Node node) {
        run(node, null);
    }


    public void run(Node node, Node ths) {

        if (node.next != null)
            for (Node next : builder.set(node).getNextList())
                run(next);

        if (node.func != null) {
            for (DataOrNode sourceParam : builder.set(node).getParams())
                if (sourceParam instanceof Node)
                    run((Node) sourceParam, ths);
            DataOrNode result = node.func.invoke(builder.set(node), ths);
            builder.set(node).setValue(result).commit();
        }

        Node source = builder.set(node).getSource();
        if (source != null) {

            DataOrNode set = builder.set(node).getSet();
            if (set != null) {
                if (set instanceof Node) {
                    Node setNode = (Node) set;
                    run(setNode, ths);
                    DataOrNode setValue = builder.set(setNode).getValue();
                    builder.set(source).setValue(setValue).commit();
                } else if (set instanceof Data) {
                    builder.set(source).setValue(set).commit();
                }
            } else {
                if (builder.set(node).getParams() != null) {
                    for (DataOrNode sourceParam : builder.set(node).getParams())
                        if (sourceParam instanceof Node)
                            run((Node) sourceParam, ths);
                    run(node, ths);
                }
            }
        }

    }

}
