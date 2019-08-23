package com.droid.djs.serialization.node;

public class HttpResponseType {
    public static final String HTML = "text/html";
    public static final String CSS = "text/css";
    public static final String JS = "text/javascript";
    public static final String PNG = "image/png";
    public static final String NUMBER_BASE10 = "number/base10";
    public static final String JSON = "application/json";
    public static final String BOOLEAN = "boolean/text";
    public static final String TEXT = "text/plain";
    public static final String NULL = "null";

    public static String fromParserName(String parserName) {
        switch (parserName) {
            case "js":
                return JS;
            case "html":
                return HTML;
            case "css":
                return CSS;
            case "png":
                return PNG;
            default:
                return null;
        }
    }
    
}
