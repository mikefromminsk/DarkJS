package com.droid.instance;


import com.droid.djs.DataStorage;
import com.droid.djs.NodeStorage;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.treads.Threads;
import com.droid.net.ftp.FtpServer;
import com.droid.net.http.HttpClientServer;
import com.droid.net.ws.WsClientServer;

import java.io.IOException;
import java.net.BindException;

public class InstanceParameters {
    public int portAdding;
    public String storeDir;
    public String nodename;
    public Long accessToken;
    public String proxyHost;
    public int proxyPortAdding;

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

    public HttpClientServer startHttpServerOnFreePort() throws BindException {
        while (httpClientServer == null && HttpClientServer.defaultPort + portAdding < 0xFFFF){
            try {
                httpClientServer = new HttpClientServer(HttpClientServer.defaultPort + portAdding);
                return httpClientServer;
            } catch (IOException e) {
                portAdding++;
            }
        }
        throw new BindException();
    }

    private HttpClientServer httpClientServer;
    public HttpClientServer getHttpClientServer() throws IOException {
        if (httpClientServer == null)
            httpClientServer = new HttpClientServer(HttpClientServer.defaultPort + portAdding);
        return httpClientServer;
    }

    private FtpServer ftpServer;
    public FtpServer startFtpServer() {
        if (ftpServer == null)
            ftpServer = new FtpServer(FtpServer.defaultPort + portAdding);
        return ftpServer;
    }

    private WsClientServer wsClientServer;
    public WsClientServer startWsClientServer() {
        if (wsClientServer == null)
            wsClientServer = new WsClientServer(WsClientServer.defaultPort + portAdding);
        return wsClientServer;
    }

    public void closeAllPorts() {
        if (httpClientServer != null)
            httpClientServer.stop();
        if (ftpServer != null)
            ftpServer.stop();
        if (wsClientServer != null)
            wsClientServer.stop();
    }

}
