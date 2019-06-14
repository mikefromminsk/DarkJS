package com.droid.djs.runner.prototypes;

import com.droid.djs.consts.NodeType;
import com.droid.djs.runner.Caller;
import com.droid.djs.runner.utils.Utils;

public class StringPrototype extends Utils {

    @Override
    public String root() {
        return DEFAULT_PROTOTYPES_DIR + NodeType.toString(NodeType.STRING);
    }

    @Override
    public void init() {
        func(Caller.STRING_TRIM_NAME, Caller.STRING_TRIM);
        func(Caller.STRING_REVERCE_NAME, Caller.STRING_REVERCE);
    }
}
