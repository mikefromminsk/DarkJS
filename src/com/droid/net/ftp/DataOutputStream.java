package com.droid.net.ftp;

import com.droid.djs.Parser;
import com.droid.djs.node.Node;
import com.droid.djs.node.NodeBuilder;
import com.droid.djs.node.NodeStyle;
import com.droid.djs.node.NodeUtils;
import com.droid.net.http.HttpServer;
import jdk.nashorn.internal.runtime.ParserException;

import java.io.*;
import java.util.Random;

public class DataOutputStream extends OutputStream {

    public final static File ftpTempDir = new File("out/FtpTemp");
    public final static Random random = new Random();
    private Node node;
    File tempFile = new File(ftpTempDir, "" + random.nextInt());
    FileOutputStream out;

    public DataOutputStream(Node node) {
        this.node = node;
        if (!ftpTempDir.exists())
            ftpTempDir.mkdirs();
        try {
            out = new FileOutputStream(tempFile);
        } catch (Exception e1) {
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void close() throws IOException {
        out.close();
        Node styleValue = NodeUtils.setStyle(node, NodeStyle.SOURCE_CODE, new FileInputStream(tempFile));
        new NodeBuilder().set(node).setValue(styleValue).commit();
        tempFile.delete();
    }
}
