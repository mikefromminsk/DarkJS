package org.fdns.requests;

public class RequestBody {
    public Integer requestId;
    public Long startTime;
    public String body;
    public Runnable success;
    public Runnable error;

    public RequestBody(Integer requestId, Long startTime, String body, Runnable success, Runnable error) {
        this.requestId = requestId;
        this.startTime = startTime;
        this.body = body;
        this.success = success;
        this.error = error;
    }
}
