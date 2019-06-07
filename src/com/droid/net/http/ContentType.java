package com.droid.net.http;

public class ContentType {
    public static final String JSON = "application/json";
    public static final String FORM_DATA = "application/x-www-form-urlencoded";

    public static String getContentTypeFromName(String filename){
        if (filename.contains(".")){
            String extention =  filename.substring(filename.lastIndexOf('.'));
            switch (extention){
                case "json": return JSON;
            }
        }
        return null;
    }
}
