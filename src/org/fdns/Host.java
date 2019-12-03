package org.fdns;

public class Host {
    String domain;
    String nextOwnerDomainHash;

    public Host(String domain, String nextOwnerDomainHash) {
        this.domain = domain;
        this.nextOwnerDomainHash = nextOwnerDomainHash;
    }
}
