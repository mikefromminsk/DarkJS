package org.fdns.requests;

import org.fdns.callbacks.ErrorCallback;
import org.fdns.callbacks.SuccessCallback;

public class RequestData {
    public Integer requestId;
    public Long startTime;
    public String data;
    public SuccessCallback success;
    public ErrorCallback error;

    public RequestData(Integer requestId, Long startTime, String data, SuccessCallback success, ErrorCallback error) {
        this.requestId = requestId;
        this.startTime = startTime;
        this.data = data;
        this.success = success;
        this.error = error;
    }
}
