package org.fdns;

import java.util.HashMap;
import java.util.Map;

public class WebSocketNetwork {

    static Map<String, WebSocketInstance> instances = new HashMap<>();

    public static void send(String domain, String data) {
        instances.get(domain).onReceive(data);
    }
}
