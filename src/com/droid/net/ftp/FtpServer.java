package com.droid.net.ftp;

import com.droid.djs.node.*;
import com.guichaguri.minimalftp.FTPConnection;
import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.api.IFTPListener;

import java.io.IOException;
import java.net.InetAddress;

public class FtpServer implements IFTPListener {

    public static NodeBuilder builder = new NodeBuilder();
    private FTPServer server = new FTPServer();

    public FtpServer start() {

        FtpAuthenticator auth = new FtpAuthenticator();

        auth.registerUser("john", "1234");
        auth.registerUser("alex", "abcd123");
        auth.registerUser("hannah", "98765");

        server.setAuthenticator(auth);
        server.addListener(new FtpServer());
        server.setTimeout(10 * 60 * 1000); // 10 minutes
        server.setBufferSize(1024 * 5); // 5 kilobytes
        try {
            server.listen(InetAddress.getByName("localhost"), 21);
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
        if (con.getFileSystem() != null)
            ((FtpSession) con.getFileSystem()).finish();
    }
}