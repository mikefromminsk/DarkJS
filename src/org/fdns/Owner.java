package org.fdns;

public class Owner {
    public String domain;
    public String token;
    public String nextTokenHash;
    public String ip;

    public Owner(String domain, String token, String nextTokenHash, String ip) {
        this.domain = domain;
        this.token = token;
        this.nextTokenHash = nextTokenHash;
        this.ip = ip;
    }
}
