package org.pdk.modules;

import java.util.List;

public class FuncInterface {
    public Func func;
    public String path;
    public String name;
    public List<String> parameters;

    public FuncInterface(Func func, String path, String name, List<String> parameters) {
        this.func = func;
        this.path = path;
        this.name = name;
        this.parameters = parameters;
    }
}
