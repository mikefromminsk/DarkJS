package org.pdk.engine.store;

public enum NodeType {
    STRING,
    NUMBER,
    BOOLEAN,
    NODE,
    ARRAY,
    OBJECT, // TODO delete and add link type CloneObject
    NATIVE_FUNCTION,
    THREAD,
}
