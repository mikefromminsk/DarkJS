package com.droid.djs.treads;

import com.droid.djs.fs.Master;
import com.droid.gdb.map.Crc16;
import com.droid.net.ftp.FtpServer;
import com.droid.net.http.HttpServer;

public class Secure {

    private static HttpServer httpServer = null;
    private static FtpServer ftpServer = null;

    public static boolean start(String login, String password) {
        Long access_owner_code = getAccessCode(login, password);

        boolean started = ThreadPool.getInstance().run(Master.getInstance(), null, false, access_owner_code);
        if (started) {
            try {
                httpServer = (HttpServer) new HttpServer(HttpServer.debugPort).start();
                ftpServer = new FtpServer().start();
            } catch (Exception e) {
                if (httpServer != null)
                    httpServer.stop();
                if (ftpServer != null)
                    ftpServer.stop();
                ThreadPool.getInstance().stop();
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
