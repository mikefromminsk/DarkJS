package org.pdk.modules;

import java.util.List;

public class FuncInterface {
    public String path;
    public String name;
    public List<String> parameters;

    public FuncInterface(String path, String name, List<String> parameters) {
        this.path = path;
        this.name = name;
        this.parameters = parameters;
    }
}
