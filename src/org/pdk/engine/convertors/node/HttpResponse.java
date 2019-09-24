package org.pdk.engine.convertors.node;

public class HttpResponse {
    public String type;
    public byte[] data;

    public HttpResponse(String type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public HttpResponse(String type, String data) {
        this.type = type;
        this.data = data.getBytes();
    }
}
