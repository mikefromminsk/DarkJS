package com.droid.djs.runner.utils;

import java.util.List;

public class FuncInterface {
    String path;
    String name;
    List<Parameter> parameters;

    public FuncInterface(String path, String name, List<Parameter> parameters) {
        this.path = path;
        this.name = name;
        this.parameters = parameters;
    }
}
