package com.droid.net.ftp;

import com.droid.djs.nodes.NodeBuilder;
import com.guichaguri.minimalftp.FTPConnection;
import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.api.IFTPListener;

import java.io.IOException;
import java.net.InetAddress;

public class FtpServer implements IFTPListener {

    public static NodeBuilder builder = new NodeBuilder();
    private FTPServer server = new FTPServer();
    public static int defaultPort = 21;
    private int port;

    public FtpServer() {
        port = defaultPort;
    }

    public FtpServer(int port) {
        this.port = port;
    }

    public FtpServer start() {
        server.setAuthenticator(new FtpAuthenticator());
        server.addListener(new FtpServer());
        server.setTimeout(3000); // 10 minutes
        server.setBufferSize(1024 * 5); // 5 kilobytes
        try {
            server.listen(InetAddress.getByName("localhost"), port);
        } catch (IOException e) {
            e.printStackTrace();
       }
        return this;
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void stop() {
        server.dispose();
    }

    @Override
    public void onConnected(FTPConnection con) {

    }

    @Override
    public void onDisconnected(FTPConnection con) {

    }
}