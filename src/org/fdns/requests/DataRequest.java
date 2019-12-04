package org.fdns.requests;

import java.util.ArrayList;
import java.util.List;

public class DataRequest extends Request {
    public List<String> path = new ArrayList<>();
    public String data;
    public Long requestTime;
    public Long responseTime;

    public DataRequest(Integer requestId, String data, Long requestTime) {
        this.requestId = requestId;
        this.data = data;
        this.requestTime = requestTime;
    }
}
