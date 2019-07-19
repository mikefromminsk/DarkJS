package com.droid.djs.treads;

import com.droid.djs.fs.Master;
import com.droid.gdb.map.Crc16;
import com.droid.net.ftp.FtpServer;
import com.droid.net.http.HttpServer;
import com.droid.net.ws.WsClientServer;
import com.droid.net.ws.WsServer;

import java.io.IOException;

public class Secure {

    private static HttpServer http = null;
    private static FtpServer ftp = null;
    private static WsClientServer ws = null;

    public static boolean start(String login, String password, String nodeName) {
        Long access_owner_code = getAccessToken(login, password);

        boolean started = Threads.getInstance().run(Master.getInstance(), null, false, access_owner_code);
        if (started) {
            try {
                http = new HttpServer(HttpServer.debugPort);
                http.start();
                ftp = new FtpServer(FtpServer.defaultPort);
                ftp.start();
                ws = new WsClientServer(WsServer.defaultPort, nodeName);
                ws.start();
            } catch (Exception e) {
                if (http != null)
                    http.stop();
                if (ftp != null)
                    ftp.stop();
                if (ws != null)
                    ws.stop();
                return false;
            }
        }
        return started;
    }

    public static Long getAccessToken(String login, String password) {
        return (long) Crc16.getHash(login + password);
    }

    public static void join() throws InterruptedException {
        if (http != null)
            http.join();
    }
}
