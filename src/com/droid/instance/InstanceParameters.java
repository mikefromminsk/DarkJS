package com.droid.instance;


import com.droid.djs.DataStorage;
import com.droid.djs.NodeStorage;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.consts.NodeType;

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

    private NodeStorage nodeStorage;
    public NodeStorage getNodeStorage() {
        if (nodeStorage == null) {
            nodeStorage = new NodeStorage(Instance.get().storeDir, "node");
            nodeStorage.initStorage();
        }
        return nodeStorage;
    }

    private DataStorage dataStorage;
    public DataStorage getDataStorage() {
        if (dataStorage == null)
            dataStorage = new DataStorage();
        return dataStorage;
    }

    private Node master = null;
    public Node getMaster() {
        if (master == null){
            NodeBuilder builder = new NodeBuilder().get(0L);
            master = builder.getLocalNode(0);
            if (master == null){
                master = builder.create(NodeType.THREAD).commit();
                builder.get(0L).addLocal(master).commit();
            }
        }
        return master;
    }

    // TODO change to change instance
    public void removeInstance(){
        master = null;
    }

}
