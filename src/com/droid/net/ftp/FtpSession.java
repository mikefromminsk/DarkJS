package com.droid.net.ftp;

import com.guichaguri.minimalftp.api.IFileSystem;
import com.droid.djs.node.*;

import java.io.InputStream;
import java.io.OutputStream;

public class FtpSession implements IFileSystem<Node> {

    private Branch branch = new Branch();
    private NodeBuilder builder = new NodeBuilder();

    @Override
    public Node getRoot() {
        return Master.getInstance();
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
        return (file == null || NodeUtils.getStyle(file, NodeStyle.SOURCE_CODE) == null);
    }

    @Override
    public int getPermissions(Node file) {
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
    public byte[] getDigest(Node file, String algorithm) {
        return new byte[0];
    }

    @Override
    public Node getParent(Node file) {
        return null;
    }

    @Override
    public Node[] listFiles(Node dir) {
        if (dir != null)
            return builder.set(dir).getLocalNodes();
        return new Node[0];
    }

    @Override
    public Node findFile(String path) {
        return findFile(getRoot(), path);
    }

    @Override
    public Node findFile(Node cwd, String path) {
        return NodeUtils.getNode(cwd, path);
    }

    @Override
    public InputStream readFile(Node file, long start) {
        if (!isDirectory(file))
            return builder.set(builder.set(file).getValueNode()).getData();
        return null;
    }

    @Override
    public OutputStream writeFile(Node file, long start) {
        Node node = NodeUtils.getNode(branch.getRoot(), NodeUtils.getPath(file));
        return new DataOutputStream(branch, node);
    }

    @Override
    public void mkdirs(Node file) {
        // mkdirs exec by NodeUtils.getNode
    }

    @Override
    public void delete(Node file) {
        // don`t exist
    }

    @Override
    public void rename(Node from, Node to) {
    }

    @Override
    public void chmod(Node file, int perms) {
    }

    @Override
    public void touch(Node file, long time) {
    }

    public void finish() {
        branch.mergeWithMaster();
    }
}
