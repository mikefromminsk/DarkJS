package com.droid.instance;

import com.droid.djs.fs.Branch;
import com.droid.djs.fs.DataOutputStream;
import com.droid.djs.fs.Files;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.serialization.node.HttpResponse;
import com.droid.djs.serialization.node.NodeSerializer;
import com.droid.gdb.map.Crc16;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Instance extends InstanceParameters implements Runnable {

    private static Map<Long, InstanceParameters> allInstanceParameters = new HashMap<>();
    private Thread instanceThread;

    public static InstanceParameters get() {
        return allInstanceParameters.get(Thread.currentThread().getId());
    }

    public Instance(int portAdding, String storeDir, String installDir, String nodename, String proxyHost, String login, String password) {
        this.portAdding = portAdding;
        this.storeDir = storeDir;
        this.nodename = nodename;
        this.proxyHost = proxyHost;
        if (login != null && password != null)
            this.accessToken = Crc16.getHash(login + password);
        load(installDir);
    }

    public Instance(String storeDir) {
        this.storeDir = storeDir;
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        if (!directoryToBeDeleted.exists())
            return true;
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null)
            for (File file : allContents)
                deleteDirectory(file);
        return directoryToBeDeleted.delete();
    }

    public Instance(String storeDir, boolean removeExistDir) throws IOException {
        this.storeDir = storeDir;
        if (removeExistDir)
            if (!deleteDirectory(new File(storeDir)))
                throw new IOException();
    }

    public Instance setProxyHost(String proxyHost, int proxyPortAdding) {
        this.proxyHost = proxyHost;
        this.proxyPortAdding = proxyPortAdding;
        return this;
    }

    public Instance setNodeName(String nodename) {
        this.nodename = nodename;
        return this;
    }

    public Instance setAccessCode(String john, String s) {
        return this;
    }

    public Instance start() {
        if (instanceThread == null || !instanceThread.isAlive()) {
            instanceThread = new Thread(this);
            instanceThread.start();
        }
        return this;
    }

    public Instance startAndWaitInit() {
        call(null);
        return this;
    }

    public void stop() {
        if (instanceThread != null) {
            instanceThread.interrupt();
            instanceThread = null;
        }
    }

    public static void connectThread(InstanceParameters instanceParameters) {
        allInstanceParameters.put(Thread.currentThread().getId(), instanceParameters);
    }

    public static void connectThreadByPortAdditional(int addToPort) {
        for (Long threadID : allInstanceParameters.keySet())
            if (allInstanceParameters.get(threadID).portAdding == addToPort) {
                connectThread(allInstanceParameters.get(threadID));
                return;
            }
        throw new NullPointerException();
    }

    public static void disconnectThread() {
        allInstanceParameters.remove(Thread.currentThread().getId());
    }

    private Runnable func;
    private Object onInitializing = new Object();
    private Object onStart = new Object();
    private Object onFinish = new Object();

    @Override
    public void run() {
        try {
            connectThread(this);

            if (loadList.size() > 0) {
                Branch loadingBranch = new Branch();
                for (String path : loadList.keySet()) {
                    InputStream inputStream = loadList.get(path);
                    if (inputStream != null) {
                        loadStream(loadingBranch, path, inputStream);
                        Node module = Files.getNode(loadingBranch.getRoot(), path);
                        Instance.get().getThreads().run(module, null);
                    } else
                        loadDirectory(loadingBranch, new File(path), "root");
                }
                loadingBranch.mergeWithMaster();
            }

            //testRootIndex();

            Instance.get().getThreads().run(Instance.get().getMaster(), null, false, accessToken);

            Instance.get().startHttpServerOnFreePort();
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
                try {
                    obj.notify();
                } catch (Exception ignore) {
                }
            }
    }

    void wait(Object obj) {
        if (obj != null)
            synchronized (obj) {
                try {
                    obj.wait();
                } catch (Exception ignore) {
                }
            }
    }

    public Instance call(Runnable func) {
        start();
        wait(onInitializing);
        this.func = func;
        notify(onStart);
        wait(onFinish);
        return this;
    }

    public HttpResponse call(String nodePath, Object... parameters) {
        HttpResponse[] response = {null};
        call(() -> {
            NodeBuilder builder = new NodeBuilder();
            Node[] nodeParameters = new Node[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object obj = parameters[i];
                if (obj instanceof String)
                    nodeParameters[i] = builder.createString((String) obj);
                else if (obj instanceof Double || obj instanceof Integer || obj instanceof Long)
                    nodeParameters[i] = builder.createNumber((double) obj);
                else if (obj instanceof Boolean)
                    nodeParameters[i] = builder.createBool((Boolean) obj);
            }
            Node calledFunction = Files.getNode(nodePath);
            Instance.get().getThreads().run(calledFunction, nodeParameters, false, accessToken);
            response[0] = NodeSerializer.getResponse(calledFunction);
        });
        return response[0];
    }

    private static void testRootIndex() {
        System.out.println("loading " + (Files.getNodeIfExist("/root/index") != null ? "success" : "fail"));
    }

    private Map<String, InputStream> loadList = new HashMap<>();

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
            DataOutputStream dataOutputStream = new DataOutputStream(loadingBranch,
                    Files.getNode(loadingBranch.getRoot(), localFileName));
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
}
