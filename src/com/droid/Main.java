package com.droid;

import com.droid.djs.node.NodeStorage;
import com.droid.net.ftp.FtpTest;
import com.droid.net.http.HttpTest;

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
