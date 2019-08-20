package com.droid;

import com.droid.instance.Instance;

public class Main {

    public static void main(String[] args) {
        new Instance("out/SimpleGraphDB")
                .load("C:/wamp/www/droid").setAccessCode("john", "123").start();
    }
}
