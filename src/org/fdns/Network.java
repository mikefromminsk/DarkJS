package org.fdns;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

public class Network {

    static Map<String, Instance> instances = new HashMap<>();

    public void send(String ip, String data) throws ConnectException {
        Instance instance = instances.get(ip);
        if (instance == null)
            throw new ConnectException();
        else
            instance.onReceive(data);
    }

    public Instance host(String ip) {
        Instance instance = new Instance(this, ip);
        instances.put(ip, instance);
        return instance;
    }

    public void clear() {
        instances.clear();
    }
}
