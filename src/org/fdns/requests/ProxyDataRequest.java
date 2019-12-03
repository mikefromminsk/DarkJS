package org.fdns.requests;

import java.util.ArrayList;
import java.util.List;

public class ProxyDataRequest extends Request {
    public List<String> path = new ArrayList<>();
    public String data;

    public ProxyDataRequest(String data) {
        this.data = data;
    }
}
