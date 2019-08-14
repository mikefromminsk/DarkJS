package com.droid.instance;

import com.droid.djs.fs.Branch;
import com.droid.djs.fs.DataOutputStream;
import com.droid.djs.fs.Files;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class Instance implements Runnable {

    private InstanceParameters instanceParameters;
    private String installDir;
    private static Map<Long, InstanceParameters> parameters = new HashMap<>();

    public static InstanceParameters get() {
        return parameters.get(Thread.currentThread().getId());
    }

    public Instance(InstanceParameters instanceParameters) {
        this.instanceParameters = instanceParameters;
        new Thread(this).start();
    }

    public Instance(InstanceParameters instanceParameters, String installDir) {
        this.installDir = installDir;
        this.instanceParameters = instanceParameters;
        new Thread(this).start();
    }

    public static void connectThread(InstanceParameters instanceParameters) {
        parameters.put(Thread.currentThread().getId(), instanceParameters);
    }

    public static void connectThreadByPortAdditional(int addToPort) {
        for (Long threadID : parameters.keySet())
            if (parameters.get(threadID).instanceID == addToPort) {
                connectThread(parameters.get(threadID));
                return;
            }
        throw new NullPointerException();
    }

    public static void disconnectThread() {
        parameters.remove(Thread.currentThread().getId());
    }

    private Runnable func;
    private Object onInitializing = new Object();
    private Object onStart = new Object();
    private Object onFinish = new Object();

    @Override
    public void run() {
        try {
            connectThread(instanceParameters);

            if (installDir != null) {
                loadingBranch = new Branch();
                loadProject(installDir, "root", false);
                loadingBranch.mergeWithMaster();
                testRootIndex();
            }

            Instance.get().getThreads().run(Instance.get().getMaster(), null, false, instanceParameters.accessToken);

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
            notify(onInitializing);
            onInitializing = null;
            notify(onStart);
            onStart = null;
            notify(onFinish);
            onFinish = null;
        } finally {
            Instance.get().closeAllPorts();
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

    public void call(Runnable func) {
        wait(onInitializing);
        this.func = func;
        notify(onStart);
        wait(onFinish);
    }

    private static void testRootIndex() {
        System.out.println("loading " + (Files.getNodeIfExist("/root/index") != null ? "success" : "fail"));
    }

    private Branch loadingBranch;

    private void loadProject(String projectPath, String localPath, boolean deleteDir) {
        try {
            File root = new File(projectPath);
            File[] list = root.listFiles();
            if (list == null) return;
            if (localPath.equals("root"))
                System.out.println("create load " + projectPath);
            for (File file : list) {
                String localFileName = localPath + "/" + file.getName();
                if (file.isDirectory()) {
                    loadProject(file.getAbsolutePath(), localFileName, deleteDir);
                } else {
                    DataOutputStream dataOutputStream = new DataOutputStream(loadingBranch,
                            Files.getNode(loadingBranch.getRoot(), localFileName));
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fileInputStream.read(buffer)) != -1)
                        dataOutputStream.write(buffer, 0, len);
                    fileInputStream.close();
                    dataOutputStream.close();
                    if (deleteDir)
                        file.delete();
                }
            }
            if (deleteDir)
                root.delete();
            if (localPath.equals("root"))
                System.out.println("finish load " + projectPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
