package com.data;

import java.util.HashMap;

public class Error implements IError {
    private final HashMap<String,String> fields=new HashMap<>();
    public void set(String key,String value){
        fields.put(key,value);
    }
}
