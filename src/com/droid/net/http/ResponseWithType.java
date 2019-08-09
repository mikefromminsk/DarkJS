package com.droid.net.http;

public class ResponseWithType {
    String type;
    byte[] data;

    public ResponseWithType(String type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public ResponseWithType(String type, String data) {
        this.type = type;
        this.data = data.getBytes();
    }
}
