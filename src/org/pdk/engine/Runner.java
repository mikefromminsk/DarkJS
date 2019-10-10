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

        for (Node next : builder.set(node).getNextList()) {
            run(next);
        }

        if (builder.set(node).isNativeFunction()) {
            for (DataOrNode sourceParam : builder.getParams())
                if (sourceParam instanceof Node)
                    run((Node) sourceParam, ths);
            DataOrNode result = node.func.invoke(builder.set(node), ths);
            builder.set(node).setValue(result).commit();
        }

        Node source = builder.set(node).getSource();
        if (source != null) {

            DataOrNode setVal = builder.set(node).getSet();
            if (setVal != null) {
                builder.set(source).setValue(setVal).commit();
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
