package com.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseHandler {
    private boolean messageStatus =true;
    private boolean errorStatus =true;
    private int verdict=1;

    public void check(String s){
        if (verdict==1) checkMessage(s);
        else if (verdict==2) checkError(s);
        else throw new IllegalArgumentException("Just in case");
    }

    public boolean success(){
        return messageStatus && errorStatus;
    }

    public boolean hasVerdict(){
        return verdict==4;
    }

    private void checkMessage(String message){
        Pattern pattern=Pattern.compile("Successfully received(\\s+)?");
        Matcher matcher=pattern.matcher(message);
        if (!matcher.find()){
            pattern=Pattern.compile("An error occurred on the server:(\\s+)?(.+)");
            matcher=pattern.matcher(message);
            if (matcher.find()) messageStatus =false;
            else throw new IllegalArgumentException("Unexpected response: "+message);
        }else {
            if (!matcher.group().equals(message)) checkError(message.replace(matcher.group(),""));
        }
        verdict<<=1;
    }
    private void checkError(String error){
        Pattern pattern=Pattern.compile("The error(\\s+)?\"(([a-zA-z|0-9]+):(\\s+)?(.+)(\\s+)?){5,}\"(\\s+)? was successfully stored(\\s+)?");
        Matcher matcher=pattern.matcher(error);
        if (!matcher.find()){
            pattern=Pattern.compile("Exception (.+) occurred while storing the following error(\\s+)?\"(([a-zA-z|0-9]+):(\\s+)?(.+)){5,}\"(\\s+)?");
            matcher=pattern.matcher(error);
            if (matcher.find()) errorStatus=false;
            else throw new IllegalArgumentException("Unexpected response: "+error);
        }
        verdict<<=1;
    }
}
