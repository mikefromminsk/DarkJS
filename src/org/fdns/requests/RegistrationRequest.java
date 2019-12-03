package org.fdns.requests;

import org.fdns.Host;

public class RegistrationRequest extends Request {
    public Host host;

    public RegistrationRequest(Host host) {
        this.host = host;
    }
}
