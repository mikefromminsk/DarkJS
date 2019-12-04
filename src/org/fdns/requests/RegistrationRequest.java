package org.fdns.requests;

import org.fdns.Owner;

public class RegistrationRequest extends Request {
    public Owner owner;

    public RegistrationRequest(Owner owner) {
        this.owner = owner;
    }
}
