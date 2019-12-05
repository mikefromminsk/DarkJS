package org.fdns;

public class Owner {
    public String domain;
    public String nextDomain;
    public String nextDomainHash;
    public String ip;

    public Owner(String domain, String nextDomain, String nextDomainHash, String ip) {
        this.domain = domain;
        this.nextDomain = nextDomain;
        this.nextDomainHash = nextDomainHash;
        this.ip = ip;
    }
}
