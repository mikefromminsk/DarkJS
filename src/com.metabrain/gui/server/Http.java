package com.metabrain.gui.server;


public class Http extends NanoHTTPD {


    public Http(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "hello world");
    }

}
