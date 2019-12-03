package org.fdns.requests;

public class RequestBody {
    public Integer requestId;
    public Long startTime;
    public String data;
    public Runnable success;
    public Runnable error;

    public RequestBody(Integer requestId, Long startTime, String data, Runnable success, Runnable error) {
        this.requestId = requestId;
        this.startTime = startTime;
        this.data = data;
        this.success = success;
        this.error = error;
    }
}
