package org.pdk.files;

import org.pdk.store.nodes.Node;
import org.pdk.instance.Instance;

import java.io.*;
import java.util.Random;

public class DataOutputStream extends OutputStream {


    public final static File ftpTempDir = new File("out/FtpTemp");
    public final static Random random = new Random();
    private Node node;
    private File tempFile = new File(ftpTempDir, "" + random.nextInt());
    private FileOutputStream out;
    private Branch branch;
    private Instance instance;

    public DataOutputStream(Instance instance, Branch branch, Node node) {
        this.instance = instance;
        this.branch = branch;
        this.node = node;
        try {
            out = new FileOutputStream(tempFile);
        } catch (Exception ignore) {
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        branch.updateTimer();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        branch.updateTimer();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        branch.updateTimer();
    }


    @Override
    public void close() {
        try {
            out.close();
            branch.updateTimer();
            boolean connectionOpened = Instance.connectThreadIfNotConnected(instance);
            Files.putFile(node, new FileInputStream(tempFile));
            if (connectionOpened)
                Instance.disconnectThread();
            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
