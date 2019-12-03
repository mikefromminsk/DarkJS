package org.fdns;

public class Host {
    String domain;
    String nextOwnerDomainHash;
    String ownerIp;

    public Host(String domain, String nextOwnerDomainHash, String ownerIp) {
        this.domain = domain;
        this.nextOwnerDomainHash = nextOwnerDomainHash;
        this.ownerIp = ownerIp;
    }
}
