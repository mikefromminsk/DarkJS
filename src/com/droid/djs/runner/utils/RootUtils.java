package com.droid.djs.runner.utils;

import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.instance.Instance;

import java.io.IOException;

public class RootUtils extends Utils {
    @Override
    public String name() {
        return "/";
    }

    @Override
    public void methods() {
        func("get",
                (builder, node, ths) -> {
                    String url = getString(builder, 0);
                    Node argObj = getNode(builder, 1);
                    Node successCallback = getNode(builder, 2);
                    Node errorCallback = getNode(builder, 3);
                    if (successCallback == null){
                        try {
                            Instance.get().getHttpClientServer().request(url, argObj);
                        } catch (IOException e) {
                            if (errorCallback != null)
                                Instance.get().getThreads().run(errorCallback, new Node[]{builder.createString(e.getMessage())}, true);
                            e.printStackTrace();
                        }
                    }else{
                        /*try {
                            Instance.get().startWsClientServer().request(url, argObj);
                        } catch (IOException e) {
                            if (errorCallback != null)
                                Instance.get().getThreads().run(errorCallback, new Node[]{builder.createString(e.getMessage())}, true);
                            e.printStackTrace();
                        }*/
                    }
                    return builder.createBool(true);
                },
                par("url", NodeType.STRING),
                par("args", NodeType.NODE),
                par("success", NodeType.NODE),
                par("error", NodeType.NODE));

        func("data", (builder, node, ths) -> {
            return null;
        }, par("hash", NodeType.STRING));

        // TODO add dynamic count of params
        func("gui", (builder, node, ths) -> {
                    Instance.get().startWsClientServer().sendGui(node);
                    return null;
                }, par("observer_id", NodeType.STRING),
                par("key", NodeType.NODE),
                par("value", NodeType.NODE));
    }
}
