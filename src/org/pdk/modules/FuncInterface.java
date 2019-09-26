package org.pdk.modules;

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
