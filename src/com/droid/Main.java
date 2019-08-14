package com.droid;

import com.droid.instance.Instance;

public class Main {

    public static void main(String[] args) {
        new Thread(new Instance(
                0,
                "out/SimpleGraphDB",
                "C:/wamp/www/droid",
                null,
                null,
                "john", "123")).start();
    }
}
