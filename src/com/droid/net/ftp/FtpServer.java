package com.droid.net.ftp;

import com.droid.djs.node.*;
import com.guichaguri.minimalftp.FTPConnection;
import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.api.IFTPListener;
import com.droid.net.auth.UserbaseAuthenticator;

import java.io.IOException;
import java.net.InetAddress;

public class FtpServer implements IFTPListener {

    public static NodeBuilder builder = new NodeBuilder();

    static Node addDir(Long nodeId, String titleStr){
        Node title = builder.create(NodeType.STRING).setData(titleStr).commit();
        Node local = builder.create().setTitle(title).setLocalParent(nodeId).commit();
        builder.get(nodeId).addLocal(local).commit();
        return local;
    }

    static void initTestStorage() {
        if (builder.get(0L).getLocalCount() == 0){
            NodeUtils.putNode("first");
            NodeUtils.putNode( "second");
            NodeUtils.putFile(0L, "firstFile", "fileData");
            Node third = NodeUtils.putNode("third");
            NodeUtils.putNode(third, "t1");
            NodeUtils.putNode(third, "t2");
            NodeUtils.putNode(third, "t3");
            NodeUtils.putNode(third, "t4");
            NodeUtils.putFile(third, "firstFile2", "fileData2");
            NodeStorage.getInstance().transactionCommit();
        }
    }

    private FTPServer server = new FTPServer();
    public FtpServer start() {

        UserbaseAuthenticator auth = new UserbaseAuthenticator();

        initTestStorage();
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

    @Override
    public void onConnected(FTPConnection con) {

    }

    @Override
    public void onDisconnected(FTPConnection con) {
        // You can use this event to dispose resources related to the connection
        // As the instance of CommandHandler is only held by the command, it will
        // be automatically disposed by the JVM
    }

    public void stop() {
        server.dispose();
    }
}