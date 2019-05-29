package com.metabrain.djs.node;

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
        NodeBuilder builder = new NodeBuilder();
        InputStream in = new FileInputStream(tempFile);
        Node data = builder.create(NodeType.STRING).setData(in).commit();
        builder.set(node).setValue(data).commit();
        tempFile.delete();
    }
}
