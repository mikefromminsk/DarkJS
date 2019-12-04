package org.fdns;

import org.fdns.callbacks.ServeCallback;

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

    public Instance get(String ip) {
        return instances.get(ip);
    }

    public Instance host(String ip, ServeCallback serveCallback) {
        Instance instance = new Instance(this, ip, serveCallback);
        instances.put(ip, instance);
        return instance;
    }

    public void clear() {
        instances.clear();
    }
}
