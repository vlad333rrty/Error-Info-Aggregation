package com.rest;

import com.data.Error;
import com.data.IError;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ErrorParser {
    public static IError parse(String s) throws ServerException{
        if (s.isEmpty()) return null;
        Pattern p=Pattern.compile("([a-zA-z|0-9]+):(\\s+)?(.+)");
        Matcher m=p.matcher(s);
        Error e=new Error();
        while (m.find()){
            e.set(m.group(1),m.group(3));
        }
        if (e.getFields().length==0) throw new ServerException(new IllegalArgumentException("Wrong error grammar"));
        return e;
    }
}
