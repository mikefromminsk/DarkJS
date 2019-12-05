package org.fdns.requests;

import java.util.ArrayList;
import java.util.List;

public class PathRequest extends Request {
    public String domain;
    public Boolean isFail;
    public List<String> trace = new ArrayList<>();
    public List<String> backtrace = new ArrayList<>();

    public String token;
    public String nextIp;

    public PathRequest(String domain, String token, String nextIp) {
        this.domain = domain;
        this.token = token;
        this.nextIp = nextIp;
    }
}
