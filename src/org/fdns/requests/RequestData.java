package org.fdns.requests;

import org.fdns.Instance;

public class RequestData {
    public Integer requestId;
    public Long startTime;
    public String data;
    public Instance.SuccessCallback success;
    public Instance.ErrorCallback error;

    public RequestData(Integer requestId, Long startTime, String data, Instance.SuccessCallback success, Instance.ErrorCallback error) {
        this.requestId = requestId;
        this.startTime = startTime;
        this.data = data;
        this.success = success;
        this.error = error;
    }
}
