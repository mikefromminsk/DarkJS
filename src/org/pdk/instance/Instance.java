package org.pdk.instance;

import org.pdk.engine.modules.root.*;
import org.pdk.engine.modules.utils.FuncInterface;
import org.pdk.engine.modules.utils.Module;
import org.pdk.engine.store.Storage;
import org.pdk.engine.fs.Branch;
import org.pdk.engine.fs.DataOutputStream;
import org.pdk.engine.fs.Files;
import org.pdk.engine.store.nodes.Node;
import org.pdk.engine.store.nodes.NodeBuilder;
import org.pdk.engine.store.nodes.ThreadNode;
import org.pdk.engine.modules.utils.Func;
import org.pdk.engine.modules.prototypes.StringPrototype;
import org.pdk.engine.convertors.node.HttpResponse;
import org.pdk.engine.convertors.node.HttpResponseType;
import org.pdk.engine.convertors.node.NodeSerializer;
import org.pdk.engine.treads.Threads;
import org.pdk.gdb.DiskManager;
import org.pdk.gdb.map.Crc16;
import org.pdk.net.ftp.FtpServer;
import org.pdk.net.http.HttpClientServer;
import org.pdk.net.ws.WsClientServer;

import java.io.*;
import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Instance implements Runnable {
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

    // todo add disconnect instance after timeout
    private static Map<Long, Instance> allConnectedThreads = new HashMap<>();
    private Thread instanceThread;
    private Map<String, InputStream> loadList = new HashMap<>();
    private List<String> loadExceptList = new ArrayList<>();
    private Runnable func;
    private Object onInitializing = new Object();
    private Object onStart = new Object();
    private Object onFinish = new Object();
    private Object onStop = new Object();

    public static Instance get() {
        return allConnectedThreads.get(Thread.currentThread().getId());
    }

    public Instance(String storeDir, boolean removeExistDir) throws IOException {
        this.storeDir = storeDir;
        if (removeExistDir)
            if (!deleteDirectory(new File(storeDir)))
                throw new IOException();
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null)
            for (File file : allContents)
                deleteDirectory(file);
        if (!directoryToBeDeleted.delete())
            System.out.println("cannot delete file " + directoryToBeDeleted.getAbsolutePath());
        return true;
    }

    public Instance setProxy(String proxyHost, int proxyPortAdding, String nodename) {
        this.proxyHost = proxyHost;
        this.proxyPortAdding = proxyPortAdding;
        this.nodename = nodename;
        return this;
    }

    public Instance setAccessCode(String login, String password) {
        this.login = login;
        this.password = password;
        this.accessToken = Crc16.getHash(login + password);
        return this;
    }

    public Instance start() {
        call(null);
        return this;
    }

    public void stop() {
        if (instanceThread != null) {
            instanceThread.interrupt();
            wait(onStop);
        }
    }

    public static Instance getByPortAdditional(int portAdding) {
        for (Long threadID : allConnectedThreads.keySet())
            if (allConnectedThreads.get(threadID).portAdding == portAdding)
                return allConnectedThreads.get(threadID);
        return null;
    }

    public static Instance connectThreadByPortAdditional(int portAdding) {
        Instance instance = getByPortAdditional(portAdding);
        connectThread(instance);
        return instance;
    }


    public static boolean connectThreadIfNotConnected(Instance instance) {
        if (allConnectedThreads.containsKey(Thread.currentThread().getId()))
            return false;
        connectThread(instance);
        return true;
    }

    public static void connectThread(Instance instance) {
        allConnectedThreads.put(Thread.currentThread().getId(), instance);
    }

    public static void disconnectThreadByPortAdding(int portAdding) {
        disconnectThread(getByPortAdditional(portAdding));
    }

    public static void disconnectThread(Instance instance) {
        allConnectedThreads.values().remove(instance);
    }

    public static void disconnectThread() {
        allConnectedThreads.remove(Thread.currentThread().getId());
    }

    public List<Func> getFunctions() {
        if (functions.size() == 0) {
            new StringPrototype();
            new ThreadModule();
            new MathModule();
            new RootModule();
            new NodeModule();
            new ConsoleModule();
            Module.saveInterfaces();
        }
        return functions;
    }

    private Storage storage;
    public Storage getStorage() {
        if (storage == null) {
            storage = new Storage(Instance.get().storeDir, "node");
            if (storage.isEmpty()){
                storage.add(new ThreadNode());
                getFunctions();
            }
        }
        return storage;
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

    private HttpClientServer httpClientServer;
    public HttpClientServer startHttpServerOnFreePort() throws BindException {
        if (httpClientServer == null){
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

    public void closeAllPorts() throws IOException, InterruptedException {
        if (httpClientServer != null)
            httpClientServer.stop();
        if (ftpServer != null)
            ftpServer.stop();
        if (wsClientServer != null)
            wsClientServer.stop();
    }

    @Override
    public void run() {
        try {
            connectThread(this);

            if (loadList.size() > 0) {
                Branch loadingBranch = new Branch(0);
                for (String path : loadList.keySet()) {
                    InputStream inputStream = loadList.get(path);
                    if (inputStream != null)
                        loadStream(loadingBranch, path, inputStream);
                    else
                        loadDirectory(loadingBranch, new File(path), "");
                }
                loadingBranch.mergeWithMaster();
            }

            //testRootIndex();

            Instance.get().startHttpServerOnFreePort();
            System.out.println("start instance " + storeDir + " http port " + (HttpClientServer.defaultPort + portAdding));
            Instance.get().startFtpServer();
            Instance.get().startWsClientServer();

            notify(onInitializing);
            onInitializing = null;
            while (true) {
                wait(onStart);
                if (func != null)
                    func.run();
                notify(onFinish);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            notify(onInitializing);
            onInitializing = null;
            notify(onStart);
            onStart = null;
            notify(onFinish);
            onFinish = null;
            //Instance.get().closeAllPorts();
            disconnectThread();
        }
    }

    void notify(Object obj) {
        if (obj != null)
            synchronized (obj) {
                obj.notify();
            }
    }

    void wait(Object obj) {
        if (obj != null)
            synchronized (obj) {
                try {
                    obj.wait();
                } catch (Exception e) {
                    getThreads().stopAllThreads();
                    try {
                        getStorage().transactionCommit();
                        getStorage().close();
                        DiskManager.removeInstance(getStorage().getDiskManager());
                    } catch (IOException e1) {
                        e.printStackTrace();
                    } finally {
                        notify(onStop);
                    }
                    System.out.println("stop instance " + storeDir);
                }
            }
    }

    public Instance call(Runnable func) {
        if (instanceThread == null || !instanceThread.isAlive()) {
            instanceThread = new Thread(this);
            instanceThread.start();
        }
        wait(onInitializing);
        this.func = func;
        notify(onStart);
        wait(onFinish);
        return this;
    }

    public HttpResponse get(String nodePath, Object... parameters) {
        final HttpResponse[] calledFunctionResponse = new HttpResponse[1];
        call(() -> {
            NodeBuilder builder = new NodeBuilder();
            Node[] nodeParameters = new Node[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object obj = parameters[i];
                if (obj instanceof String)
                    nodeParameters[i] = builder.createString((String) obj);
                else if (obj instanceof Integer)
                    nodeParameters[i] = builder.createNumber((double) (int) obj);
                else if (obj instanceof Double)
                    nodeParameters[i] = builder.createNumber((double) obj);
                else if (obj instanceof Long)
                    nodeParameters[i] = builder.createNumber((double) (long) obj);
                else if (obj instanceof Boolean)
                    nodeParameters[i] = builder.createBool((Boolean) obj);
            }
            Node node = Files.getNodeIfExist(nodePath);

            if (node != null) {
                Instance.get().getThreads().run(node, nodeParameters, false, accessToken);
                node = builder.set(node).getValueNode();
                calledFunctionResponse[0] = NodeSerializer.getResponse(node);
            }
        });
        return calledFunctionResponse[0];
    }

    public Double getNumber(String nodePath, Object... parameters) {
        HttpResponse response = get(nodePath, parameters);
        if (response.type.equals(HttpResponseType.NUMBER_BASE10))
            return Double.valueOf(new String(response.data));
        return null;
    }

    public String getString(String nodePath, Object... parameters) {
        return new String(get(nodePath, parameters).data);
    }

    public void run(String nodePath, Object... parameters) {
        get(nodePath, parameters);
    }

    public Instance load(String path, String filedata) {
        if (path != null && filedata != null)
            loadList.put(path, new ByteArrayInputStream(filedata.getBytes()));
        return this;
    }

    public Instance load(String dirPath) {
        if (dirPath != null)
            loadList.put(dirPath, null);
        return this;
    }

    private void loadStream(Branch loadingBranch, String localFileName, InputStream dataStream) {
        try {
            if (loadExceptList.contains(localFileName)) {
                dataStream.close();
                return;
            }
            Instance.get();
            Node file = Files.getNode(loadingBranch.getRoot(), localFileName);
            DataOutputStream dataOutputStream = new DataOutputStream(Instance.get(), loadingBranch, file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = dataStream.read(buffer)) != -1)
                dataOutputStream.write(buffer, 0, len);
            dataStream.close();
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDirectory(Branch loadingBranch, File root, String localPath) {
        try {
            File[] list = root.listFiles();
            if (list == null) return;
            for (File file : list) {
                String localFileName = localPath + "/" + file.getName();
                if (loadExceptList.contains(localFileName))
                    continue;
                if (file.isDirectory()) {
                    loadDirectory(loadingBranch, file, localFileName);
                } else {
                    loadStream(loadingBranch, localFileName, new FileInputStream(file));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Instance loadExcept(String exception) {
        exception = exception.replace('\\', '/');
        if (exception.charAt(0) != '/')
            exception = '/' + exception;
        loadExceptList.add(exception);
        return this;
    }
}
