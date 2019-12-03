package org.fdns.requests;

import java.util.Random;

public class Request {
    private static Random random = new Random();
    public Integer requestId = random.nextInt();
    public String requestType;

    public Request() {
        requestType = this.getClass().getSimpleName();
    }
}
