package org.fdns.requests;

public class Request {
    public String requestType;

    public Request() {
        requestType = this.getClass().getSimpleName();
    }
}
