package com.metabrain;

import com.metabrain.djs.node.NodeStorage;
import com.metabrain.net.ftp.FtpTest;
import com.metabrain.net.http.HttpTest;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        NodeStorage.getInstance();
        FtpTest.start();
        HttpTest.start();
        uploadGui();
    }

    private static void uploadGui() {

    }
}
