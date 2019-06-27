package com.droid.djs.treads;

import com.droid.djs.fs.Master;
import com.droid.gdb.map.Crc16;
import com.droid.net.ftp.FtpServer;
import com.droid.net.http.HttpServer;
import com.droid.net.ws.WsServer;

import java.io.IOException;

public class Secure {

    private static HttpServer httpServer = null;
    private static FtpServer ftpServer = null;
    private static WsServer wsServer = null;

    public static boolean start(String login, String password) {
        Long access_owner_code = getAccessCode(login, password);

        boolean started = Threads.getInstance().run(Master.getInstance(), null, false, access_owner_code);
        if (started) {
            try {
                httpServer = new HttpServer(HttpServer.debugPort);
                httpServer.start();
                ftpServer = new FtpServer(FtpServer.defaultPort);
                ftpServer.start();
                wsServer = new WsServer(WsServer.defaultPort);
                wsServer.start();
            } catch (Exception e) {
                if (httpServer != null)
                    httpServer.stop();
                if (ftpServer != null)
                    ftpServer.stop();
                if (wsServer != null) {
                    try {
                        wsServer.stop();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                return false;
            }
        }
        return started;
    }

    public static Long getAccessCode(String login, String password) {
        return (long) Crc16.getHash(login + password);
    }

    public static void join() throws InterruptedException {
        if (httpServer != null)
            httpServer.join();
    }
}
