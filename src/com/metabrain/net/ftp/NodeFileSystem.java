package com.metabrain.net.ftp;

import com.guichaguri.minimalftp.api.IFileSystem;
import com.metabrain.djs.node.DataStream;
import com.metabrain.djs.node.Node;
import com.metabrain.djs.node.NodeBuilder;
import com.metabrain.djs.node.NodeType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

public class NodeFileSystem implements IFileSystem<Node> {

    private NodeBuilder builder = new NodeBuilder();

    @Override
    public Node getRoot() {
        return builder.get(0L).getNode();
    }

    @Override
    public String getPath(Node file) {
        return "" + builder.set(file).getId();
    }

    @Override
    public boolean exists(Node file) {
        return true;
    }

    @Override
    public boolean isDirectory(Node file) {
        return (file == null || builder.set(file).isVar());
    }

    @Override
    public int getPermissions(Node file) {
        // Intended Format
        // -rw-rw-rw-
        // -rwxrwxrwx
        // drwxrwxrwx
        return 0;
    }

    @Override
    public long getSize(Node file) {
        if (file != null && builder.set(file).isData()) {
            return builder.getData().length;
        } else {
            return 0;
        }
    }

    @Override
    public long getLastModified(Node file) {
        return 0;
    }

    @Override
    public int getHardLinks(Node file) {
        return 0;
    }

    @Override
    public String getName(Node file) {
        return builder.set(file).getTitleString();
    }

    @Override
    public String getOwner(Node file) {
        return null;
    }

    @Override
    public String getGroup(Node file) {
        return null;
    }

    @Override
    public byte[] getDigest(Node file, String algorithm) throws IOException, NoSuchAlgorithmException {
        return new byte[0];
    }

    @Override
    public Node getParent(Node file) throws IOException {
        return null;
    }

    @Override
    public Node[] listFiles(Node dir) throws IOException {
        if (dir != null)
            return builder.set(dir).getLocalNodes();
        return new Node[0];
    }

    @Override
    public Node findFile(String path) throws IOException {
        //return findFile(getRoot(), path);
        return builder.get(Long.parseLong(path)).getNode();
    }

    @Override
    public Node findFile(Node cwd, String path) throws IOException {
        /*Node[] local = builder.set(cwd).getLocalNodes();
        for (Node node : local)
            if (path.equals(builder.set(node).getTitleString()))
                return node;*/
        try {
            return builder.get(Long.parseLong(path.replace("/", ""))).getNode();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public InputStream readFile(Node file, long start) throws IOException {
        if (builder.set(file).isData())
            return builder.getData();
        return null;
    }

    @Override
    public OutputStream writeFile(Node file, long start) throws IOException {
        return null;
    }

    @Override
    public void mkdirs(Node file) throws IOException {

    }

    @Override
    public void delete(Node file) throws IOException {
        // don`t exist
    }

    @Override
    public void rename(Node from, Node to) throws IOException {

    }

    @Override
    public void chmod(Node file, int perms) throws IOException {

    }

    @Override
    public void touch(Node file, long time) throws IOException {

    }
}
