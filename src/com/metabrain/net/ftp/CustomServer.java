package com.metabrain.net.ftp;

import com.guichaguri.minimalftp.FTPConnection;
import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.api.IFTPListener;
import com.metabrain.djs.node.Node;
import com.metabrain.djs.node.NodeBuilder;
import com.metabrain.djs.node.NodeType;

import java.io.IOException;
import java.net.InetAddress;

public class CustomServer implements IFTPListener {

    public static void main(String[] args) throws IOException {
        FTPServer server = new FTPServer();

        // Create our custom authenticator
        UserbaseAuthenticator auth = new UserbaseAuthenticator();

        initTestStorage();
        // Register a few users
        auth.registerUser("john", "1234");
        auth.registerUser("alex", "abcd123");
        auth.registerUser("hannah", "98765");

        // Set our custom authenticator
        server.setAuthenticator(auth);

        // Register an instance of this class as a listener
        server.addListener(new CustomServer());

        // Changes the timeout to 10 minutes
        server.setTimeout(10 * 60 * 1000); // 10 minutes

        // Changes the buffer size
        server.setBufferSize(1024 * 5); // 5 kilobytes

        // Start it synchronously in our localhost and in the port 21
        server.listenSync(InetAddress.getByName("localhost"), 21);

    }

    public static NodeBuilder builder = new NodeBuilder();
    static void addDir(Long nodeId, String titleStr){
        Node title = builder.create(NodeType.STRING).setData(titleStr).commit();
        Node local = builder.create().setTitle(title).commit();
        builder.get(nodeId).addLocal(local).commit();
    }

    static void initTestStorage() {
        addDir(0L, "first");
        addDir(0L, "second");
        addDir(0L, "third");
    }

    @Override
    public void onConnected(FTPConnection con) {
        // Creates our command handler
        CommandHandler handler = new CommandHandler(con);

        // Register our custom command
        con.registerCommand("CUSTOM", "CUSTOM <string>", handler::customCommand);
    }

    @Override
    public void onDisconnected(FTPConnection con) {
        // You can use this event to dispose resources related to the connection
        // As the instance of CommandHandler is only held by the command, it will
        // be automatically disposed by the JVM
    }
}