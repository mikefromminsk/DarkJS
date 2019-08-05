package com.droid.net.ws;

import java.util.List;
import java.util.Map;

public class Message {

    String destNodeName;
    String receiverPath;
    List<Map<String, Map<String, Object>>> args;
    Long accessCode;

    public Message(String destNodeName, String receiverPath, List<Map<String, Map<String, Object>>> args, Long accessCode) {
        this.destNodeName = destNodeName;
        this.receiverPath = receiverPath;
        this.args = args;
        this.accessCode = accessCode;
    }
}
