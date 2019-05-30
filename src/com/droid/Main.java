package com.droid;

import com.droid.djs.node.NodeStorage;
import com.droid.djs.node.NodeUtils;
import com.droid.net.ftp.FtpServer;
import com.droid.net.http.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        NodeStorage.getInstance();

        //uploadGui();

        HttpServer httpServer = null;
        FtpServer ftpServer = null;
        try{
            httpServer = (HttpServer) new HttpServer(HttpServer.debugPort).start();
            ftpServer = new FtpServer().start();
            httpServer.join();
        } catch (InterruptedException e) {
            httpServer.stop();
            ftpServer.stop();
        }
    }

    public static void upload(File root, File dir) {
        File[] list = dir.listFiles();
        if (list != null)
            for (File f : list) {
                if (f.isDirectory()) {
                    upload(root, f);
                } else {
                    String relativePath = dir.getAbsolutePath().substring(root.getAbsolutePath().length());
                    try {
                        NodeUtils.putFile(relativePath, new FileInputStream(f));
                    } catch (FileNotFoundException ignored) {
                    }
                }
            }
    }

    private static void uploadGui() {
        File guiDir = new File("gui");
        if (guiDir.exists()) {
            upload(guiDir, guiDir);
        }
    }
}
