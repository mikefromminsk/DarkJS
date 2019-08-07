package com.droid;

import com.droid.djs.fs.Master;
import com.droid.djs.nodes.Node;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.djs.fs.Branch;
import com.droid.djs.fs.Files;
import com.droid.djs.treads.Secure;

import com.droid.djs.fs.DataOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    public static final int VERSION = 541;

    public static final String login = "john";
    public static String password = "123";

    private static Branch loadingBranch;

    public static void main(String[] args) throws InterruptedException {
        loadingBranch = new Branch();
        //loadProject("C:/darkjs/root", "root", true);
        loadProject("C:/wamp/www/droid", "root", false);
        loadingBranch.mergeWithMaster();
        testRootIndex();
        Secure.start(login, password);
        Secure.join();
    }

    private static void testRootIndex() {
        System.out.println("loading " + (Files.getNodeIfExist("/root/index") != null ? "success" : "fail"));
    }

    private static void loadProject(String projectPath, String localPath, boolean deleteDir) {
        File root = new File(projectPath);
        File[] list = root.listFiles();
        if (list == null) return;
        if (localPath.equals("root"))
            System.out.println("start load " + projectPath);
        for (File file : list) {
            String localFileName = localPath + "/" + file.getName();
            if (file.isDirectory()) {
                loadProject(file.getAbsolutePath(), localFileName, deleteDir);
            } else {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(loadingBranch, Files.getNode(loadingBranch.getRoot(), localFileName));
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fileInputStream.read(buffer)) != -1)
                        dataOutputStream.write(buffer, 0, len);
                    fileInputStream.close();
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (deleteDir)
                    file.delete();
            }
        }
        if (deleteDir)
            root.delete();
        if (localPath.equals("root"))
            System.out.println("finish load " + projectPath);
    }
}
