package org.fdns.requests;

import java.util.ArrayList;
import java.util.List;

public class ProxyPathRequest extends Request {
    public String findDomain;
    public List<String> trace = new ArrayList<>();
    public List<String> backtrace = new ArrayList<>();

    public ProxyPathRequest(String findDomain) {
        this.findDomain = findDomain;
    }
}
