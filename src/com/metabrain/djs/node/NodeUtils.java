package com.metabrain.djs.node;

public class NodeUtils {
    public static String getPath(Node file){
        NodeBuilder builder = new NodeBuilder().set(file);
        String path = "";
        while (builder.getLocalParent() != null) {
            path += "/" + builder.getTitleString();
            Node localParent = builder.getLocalParentNode();
            builder.set(localParent);
        }
        return path;
    }
}
