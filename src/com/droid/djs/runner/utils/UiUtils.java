package com.droid.djs.runner.utils;

import com.droid.djs.consts.NodeType;

public class UiUtils extends Utils {

    public static final int SEND = 26;

    @Override
    public String name() {
        return "UI";
    }

    @Override
    public void methods() {
        func("send", SEND, par("funcname", NodeType.STRING), par("arguments", NodeType.VAR));
    }
}
