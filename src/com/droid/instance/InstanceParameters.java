package com.droid.instance;


import com.droid.djs.DataStorage;
import com.droid.djs.NodeStorage;

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

    NodeStorage nodeStorage;
    public NodeStorage getNodeStorage() {
        if (nodeStorage == null) {
            nodeStorage = new NodeStorage(Instance.get().storeDir, "node");
            nodeStorage.initStorage();
        }
        return nodeStorage;
    }

    DataStorage dataStorage;
    public DataStorage getDataStorage() {
        if (dataStorage == null)
            dataStorage = new DataStorage();
        return dataStorage;
    }


}
