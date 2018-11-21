package com.metabrain.gui;


import java.io.IOException;
import java.net.*;

public class Http extends NanoHTTPD {


    public Http(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "hello world");
    }

}
