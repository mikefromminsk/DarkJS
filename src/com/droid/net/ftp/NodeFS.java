package com.droid.net.ftp;

import com.guichaguri.minimalftp.api.IFileSystem;
import com.droid.djs.node.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

public class NodeFS implements IFileSystem<Node> {

    private NodeBuilder builder = new NodeBuilder();

    @Override
    public Node getRoot() {
        return builder.get(0L).getNode();
    }

    @Override
    public String getPath(Node file) {
        return NodeUtils.getPath(file);
    }

    @Override
    public boolean exists(Node file) {
        return true;
    }

    @Override
    public boolean isDirectory(Node file) {
        return (file == null || builder.set(file).getValueNode() == null);
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
        if (file != null) {
            Node value = builder.set(file).getValueNode();
            if (value != null && builder.set(value).isData())
                return builder.getData().length;
        }
        return 0;
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
        return findFile(getRoot(), path);
    }

    @Override
    public Node findFile(Node cwd, String path) throws IOException {
        return NodeUtils.putPath(cwd, path);
    }

    @Override
    public InputStream readFile(Node file, long start) throws IOException {
        if (!isDirectory(file))
            return builder.set(builder.set(file).getValueNode()).getData();
        return null;
    }

    @Override
    public OutputStream writeFile(Node file, long start) throws IOException {
        return new DataOutputStream(file);
    }

    @Override
    public void mkdirs(Node file) throws IOException {
        // mkdirs exec by NodeUtils.putPath
    }

    @Override
    public void delete(Node file) throws IOException {
        // don`t exist
        System.out.println(1);
    }

    @Override
    public void rename(Node from, Node to) throws IOException {
        System.out.println(1);
    }

    @Override
    public void chmod(Node file, int perms) throws IOException {
        System.out.println(1);
    }

    @Override
    public void touch(Node file, long time) throws IOException {
        System.out.println(1);
    }
}
