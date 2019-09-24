package org.pdk.engine.modules.utils;

import org.pdk.engine.modules.utils.Parameter;

import java.util.List;

public class FuncInterface {
    public String path;
    public String name;
    public List<Parameter> parameters;

    public FuncInterface(String path, String name, List<Parameter> parameters) {
        this.path = path;
        this.name = name;
        this.parameters = parameters;
    }
}
