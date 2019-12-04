package org.fdns;

public class Owner {
    public String domain;
    public String nextOwnerDomainHash;
    public String ip;

    public Owner(String domain, String nextOwnerDomainHash, String ip) {
        this.domain = domain;
        this.nextOwnerDomainHash = nextOwnerDomainHash;
        this.ip = ip;
    }
}
