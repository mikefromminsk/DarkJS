package com.droid;

import com.droid.djs.NodeStorage;
import com.droid.djs.fs.Master;
import com.droid.djs.treads.ThreadPool;
import com.droid.net.ftp.FtpServer;
import com.droid.net.http.HttpServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        HttpServer httpServer = null;
        FtpServer ftpServer = null;
        try {
            NodeStorage.getInstance();

            httpServer = (HttpServer) new HttpServer(HttpServer.debugPort).start();
            ftpServer = new FtpServer().start();

            ThreadPool.getInstance().autorun();

            httpServer.join();
        } catch (InterruptedException e) {
            httpServer.stop();
            ftpServer.stop();
        }
    }
}
