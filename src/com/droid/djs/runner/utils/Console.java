package com.droid.djs.runner.utils;

import com.droid.djs.consts.NodeType;

public class Console extends Utils {
    @Override
    public String name() {
        return "Console";
    }

    @Override
    public void methods() {
        func("log", (builder, ths) -> {
            String message = firstString(builder);
            System.out.println(message);
            return builder.createBool(true);
        }, par("message", NodeType.STRING));
    }
}
