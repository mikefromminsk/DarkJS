package com.droid;

import com.droid.instance.Instance;
import com.droid.instance.InstanceParameters;

public class Main {

    public static void main(String[] args) {
        new Thread(new Instance(new InstanceParameters(
                0,
                "out/SimpleGraphDB",
                null,
                null,
                "john", "123"), "C:/wamp/www/droid")).start();
    }
}
