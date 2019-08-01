package com.droid.net.ftp;

import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.fs.Branch;
import com.droid.djs.fs.Files;
import com.droid.djs.fs.Master;
import com.droid.djs.fs.DataOutputStream;
import com.droid.djs.treads.Secure;
import com.guichaguri.minimalftp.Utils;
import com.guichaguri.minimalftp.api.IFileSystem;
import com.droid.djs.nodes.*;

import java.io.InputStream;
import java.io.OutputStream;

public class FtpSession implements IFileSystem<Node> {

    private Branch branch = new Branch(2000);
    private NodeBuilder builder = new NodeBuilder();
    private Long access_token;

    public FtpSession(String username, String password) {
        access_token = Secure.getAccessToken(username, password);
    }

    @Override
    public Node getRoot() {
        return Master.getInstance();
    }

    @Override
    public String getPath(Node file) {
        return Files.getPath(file);
    }

    @Override
    public boolean exists(Node file) {
        return true;
    }

    @Override
    public boolean isDirectory(Node file) {
        return Files.isDirectory(file);
    }

    @Override
    public int getPermissions(Node file) {
        int perms = 0;
        perms = Utils.setPermission(perms, Utils.CAT_OWNER + Utils.TYPE_READ, true);
        perms = Utils.setPermission(perms, Utils.CAT_OWNER + Utils.TYPE_WRITE, true);
        perms = Utils.setPermission(perms, Utils.CAT_OWNER + Utils.TYPE_EXECUTE, true);
        perms = Utils.setPermission(perms, Utils.CAT_PUBLIC + Utils.TYPE_READ, true);
        perms = Utils.setPermission(perms, Utils.CAT_PUBLIC + Utils.TYPE_WRITE, true);
        perms = Utils.setPermission(perms, Utils.CAT_PUBLIC + Utils.TYPE_EXECUTE, true);
        perms = Utils.setPermission(perms, Utils.CAT_GROUP + Utils.TYPE_READ, true);
        perms = Utils.setPermission(perms, Utils.CAT_GROUP + Utils.TYPE_WRITE, true);
        perms = Utils.setPermission(perms, Utils.CAT_GROUP + Utils.TYPE_EXECUTE, true);
        return perms;
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
        if (file != null)
            return builder.set(file).getLocalParentNode();
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
        return Files.getNode(cwd, path, null, access_token);
    }

    @Override
    public InputStream readFile(Node file, long start) {
        if (!isDirectory(file))
            return builder.set(builder.set(file).getValueNode()).getData();
        return null;
    }

    @Override
    public OutputStream writeFile(Node file, long start) {
        Node node = Files.getNode(branch.getRoot(), Files.getPath(file));
        return new DataOutputStream(branch, node);
    }

    @Override
    public void mkdirs(Node file) {
    }

    @Override
    public void delete(Node file) {
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
}
