package org.fdns.requests;

public class UpdateRequest extends Request {
    String domain;
    String nextOwnerDomain;

    public UpdateRequest(String domain, String nextOwnerDomain) {
        this.domain = domain;
        this.nextOwnerDomain = nextOwnerDomain;
    }
}
