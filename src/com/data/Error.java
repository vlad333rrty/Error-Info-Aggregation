package com.data;

import java.util.Arrays;
import java.util.HashMap;

public class Error implements IError {
    private final HashMap<String,String> fields=new HashMap<>();

    public void set(String key,String value){
        fields.put(key,value);
    }

    @Override
    public String getFieldValue(String fieldName){
        return fields.get(fieldName);
    }

    @Override
    public boolean isDuplicate(IError error) {
        String[] fields = error.getFields();
        if (fields.length != getFields().length) return false;
        return Arrays.stream(fields).allMatch(s -> getFieldValue(s).equals(error.getFieldValue(s)));
    }

    @Override
    public String[] getFields() {
        return fields.keySet().toArray(String[]::new);
    }

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder();
        String[] fields=getFields();
        for (int i=0;i<fields.length-1;i++){
            builder.append(String.format("%s: %s\n",fields[i],getFieldValue(fields[i])));
        }
        builder.append(String.format("%s: %s",fields[fields.length-1],getFieldValue(fields[fields.length-1])));
        return builder.toString();
    }
}
