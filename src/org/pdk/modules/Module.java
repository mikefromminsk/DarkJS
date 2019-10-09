package org.pdk.modules;

import java.util.Arrays;

abstract public class Module {

    public Module() {
        methods();
    }

    public abstract String path();

    public abstract void methods();

    public void func(String name, Func func, String... args) {
        ModuleManager.functions.add(func);
        ModuleManager.interfaces.add(new FuncInterface((path().endsWith("/") ? path() : path() + "/"), name, Arrays.asList(args)));
    }
}
