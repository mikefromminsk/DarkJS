package com.droid.net.ws;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Message {

    Long startRequestTime;
    String destination;
    List<String> trace;
    String receiverPath;
    Map<String, Map<String, Object>> node;
    Long accessCode;

    public Message(String destination, String receiverPath, Map<String, Map<String, Object>> node, Long accessCode) {
        startRequestTime = new Date().getTime();
        this.destination = destination;
        this.receiverPath = receiverPath;
        this.node = node;
        this.accessCode = accessCode;
    }
}
