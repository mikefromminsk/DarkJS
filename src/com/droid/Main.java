package com.droid;

import com.droid.instance.Instance;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Instance("out/SimpleGraphDB", true)
                .load("C:/wamp/www/droid")
                .setAccessCode("john", "123")
                .start()
                .getHttpClientServer()
                .join();
    }
}
