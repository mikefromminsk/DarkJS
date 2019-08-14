package com.droid.instance;


public class InstanceParameters {
    public int instanceID;
    public String storeDir;
    public String nodename;
    public Long accessToken;
    public String proxyHost;

    public InstanceParameters(int instanceID, String storeDir, String nodename, Long accessToken, String proxyHost) {
        this.instanceID = instanceID;
        this.storeDir = storeDir;
        this.nodename = nodename;
        this.accessToken = accessToken;
        this.proxyHost = proxyHost;
    }
}
