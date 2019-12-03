package org.fdns.requests;

import org.fdns.Host;

public class InitializeRequest extends Request {
    public Host host;

    public InitializeRequest(Host host) {
        this.host = host;
    }
}
