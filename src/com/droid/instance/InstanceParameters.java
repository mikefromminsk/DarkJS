package com.droid.instance;


import com.droid.djs.DataStorage;
import com.droid.djs.NodeStorage;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.ThreadNode;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.runner.Func;
import com.droid.djs.runner.prototypes.StringPrototype;
import com.droid.djs.runner.utils.*;
import com.droid.djs.treads.Threads;
import com.droid.net.ftp.FtpServer;
import com.droid.net.http.HttpClientServer;
import com.droid.net.ws.WsClientServer;

import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.List;

public class InstanceParameters {
    public int portAdding;
    public String storeDir;
    public String nodename;
    public String proxyHost;
    public int proxyPortAdding;
    public Long accessToken;
    public String login;
    public String password;

    public List<Func> functions = new ArrayList<>();
    public List<FuncInterface> interfaces = new ArrayList<>();

    public List<Func> getFunctions() {
        if (functions.size() == 0) {
            new StringPrototype();
            new ThreadUtils();
            new MathUtils();
            new RootUtils();
            new NodeUtils();
            new Net();
            new Console();
            Utils.saveInterfaces();
        }
        return functions;
    }

    private NodeStorage nodeStorage;
    public NodeStorage getNodeStorage() {
        if (nodeStorage == null) {
            nodeStorage = new NodeStorage(Instance.get().storeDir, "node");
            if (nodeStorage.isEmpty()){
                nodeStorage.add(new ThreadNode());
                getFunctions();
            }
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
        if (master == null)
            master = Files.getNodeFromRoot("master");
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
