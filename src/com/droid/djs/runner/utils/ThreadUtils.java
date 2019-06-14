package com.droid.djs.runner.utils;

public class ThreadUtils extends Utils {

    public static final String PROTOTYPE_NAME = "Thread";
    public static final String SLEEP_NAME = "sleep";
    public static final int SLEEP = 15;

    @Override
    public String root() {
        return PROTOTYPE_NAME;
    }

    @Override
    public void init() {
        func(SLEEP_NAME, SLEEP);
    }
}
