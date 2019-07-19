package com.droid.net.ws;

import java.util.Map;

public class Message {
    // TODO TTL
    String destination;
    String path;
    Map<String, Map<String, Object>> nodes;

    public Message(String destination, String path, Map<String, Map<String, Object>> nodes) {
        this.destination = destination;
        this.path = path;
        this.nodes = nodes;
    }
}
