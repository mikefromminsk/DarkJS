package com.droid;

import com.droid.djs.treads.Secure;

import java.io.IOException;

public class Main {

    public static final String login = "john";
    public static String password = "123";

    public static void main(String[] args) throws InterruptedException {
        Secure.start(login, password);
        Secure.join();
    }
}
