package com.droid.net.ftp;

import com.guichaguri.minimalftp.FTPConnection;
import com.guichaguri.minimalftp.api.IFileSystem;
import com.guichaguri.minimalftp.api.IUserAuthenticator;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple user base which encodes passwords in MD5 (not really for security, it's just as an example)
 * @author Guilherme Chaguri
 */
public class FtpAuthenticator implements IUserAuthenticator {

    private final Map<String, byte[]> userbase = new HashMap<>();

    private byte[] toMD5(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(pass.getBytes("UTF-8"));
        } catch(Exception ex) {
            return pass.getBytes();
        }
    }

    public void registerUser(String username, String password) {
        userbase.put(username, toMD5(password));
    }

    @Override
    public boolean needsUsername(FTPConnection con) {
        return true;
    }

    @Override
    public boolean needsPassword(FTPConnection con, String username, InetAddress address) {
        return true;
    }

    @Override
    public IFileSystem authenticate(FTPConnection con, InetAddress address, String username, String password) throws AuthException {

        if(!userbase.containsKey(username) || !Arrays.equals(userbase.get(username), toMD5(password))) {
            throw new AuthException();
        }
        con.setFileSystem(new FtpSession());
        return con.getFileSystem();
    }
}
