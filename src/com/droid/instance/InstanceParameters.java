package com.droid.instance;


import com.droid.djs.DataStorage;
import com.droid.djs.NodeStorage;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.treads.Threads;
import com.droid.gdb.map.Crc16;
import com.droid.net.ftp.FtpServer;
import com.droid.net.http.HttpServer;
import com.droid.net.ws.WsClientServer;

import java.io.IOException;
import java.net.BindException;

public class InstanceParameters {
    public int instanceID;
    public String storeDir;
    public String nodename;
    public String proxyHost;
    public Long accessToken;

    public InstanceParameters(int addToPortNumber, String storeDir, String nodename, String proxyHost, String login, String password) {
        this.instanceID = addToPortNumber;
        this.storeDir = storeDir;
        this.nodename = nodename;
        this.proxyHost = proxyHost;
        this.accessToken =  Crc16.getHash(login + password);
    }

    public InstanceParameters(String storeDir) {
        this.storeDir = storeDir;
    }

    public InstanceParameters setNodename(String nodename) {
        this.nodename = nodename;
        return this;
    }

    public InstanceParameters setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        return this;
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

    private Threads threads;
    public Threads getThreads() {
        if (threads == null)
            threads = new Threads();
        return threads;
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
    public void removeMaster(){
        master = null;
    }

    public HttpServer startHttpServerOnFreePort() throws BindException {
        while (httpServer == null && HttpServer.defaultPort + instanceID < 0xFFFF){
            try {
                httpServer = new HttpServer(HttpServer.defaultPort + instanceID);
                return httpServer;
            } catch (IOException e) {
                e.printStackTrace();
                instanceID++;
            }
        }
        throw new BindException();
    }

    private HttpServer httpServer;
    public HttpServer startHttpServer() throws IOException {
        if (httpServer == null)
            httpServer = new HttpServer(HttpServer.defaultPort + instanceID);
        return httpServer;
    }

    private FtpServer ftpServer;
    public FtpServer startFtpServer() {
        if (ftpServer == null)
            ftpServer = new FtpServer(FtpServer.defaultPort + instanceID);
        return ftpServer;
    }

    private WsClientServer wsClientServer;
    public WsClientServer startWsClientServer() {
        if (wsClientServer == null)
            wsClientServer = new WsClientServer(WsClientServer.defaultPort + instanceID);
        return wsClientServer;
    }

    public void closeAllPorts() {
        if (httpServer != null)
            httpServer.stop();
        if (ftpServer != null)
            ftpServer.stop();
        if (wsClientServer != null)
            wsClientServer.stop();
    }
}
