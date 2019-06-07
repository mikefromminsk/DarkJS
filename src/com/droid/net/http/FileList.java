package com.droid.net.http;

import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;
import com.droid.djs.node.NodeUtils;

public class FileList {

    static String build(Node node) {
        StringBuilder response = new StringBuilder();
        NodeBuilder builder = new NodeBuilder();
        Node[] locals = builder.set(node).getLocalNodes();

        response.append("<html>");
        response.append("<body>");

        for (Node local : locals)
            response.append("<a href=\"" + NodeUtils.getPath(local) + "\">" + builder.set(local).getTitleString() + "</a><br>");

        response.append("</body>");
        response.append("</html>");

        return response.toString();
    }
}
