package com.metabrain.gui;


import java.io.IOException;
import java.net.*;

public class Http extends NanoHTTPD {


    public Http(int port) {
        super(port);
    }

    String getMac(InetAddress ip){
        try {
            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++)
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            return sb.toString();

        } catch (Exception e) {
            return "error";
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String ss;
        try {
            ss = getMac(InetAddress.getByName(session.getHeaders().get("remote-addr")));
        } catch (UnknownHostException e) {
            ss = "get";
        }
        System.out.println(session.getHeaders().get("remote-addr"));
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, ss);
    }

    public static void main(String[] args) throws InterruptedException {
        NanoHTTPD ds = new Http(20000);
        try {
            ds.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.sleep(100000);
    }
}
