package com.droid.net.ftp;

import com.droid.instance.Instance;
import com.droid.net.ftp.FtpSession;
import com.guichaguri.minimalftp.FTPConnection;
import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.api.IFileSystem;
import com.guichaguri.minimalftp.api.IUserAuthenticator;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple user base which encodes passwords in MD5 (not really for security, it's just as an example)
 *
 * @author Guilherme Chaguri
 */
public class FtpAuthenticator implements IUserAuthenticator {
    // TODO add html auth support

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
        if (con.getFileSystem() == null){
            FtpSession ftpSession = new FtpSession(con.getServer().getPort(), username, password);
            con.setFileSystem(ftpSession);
        }
        return con.getFileSystem();
    }
}
