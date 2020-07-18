package com.data;

public class Event {
    public Type type;
    public Event(Type type){
        this.type=type;
    }
    public enum Type {
        GET_INFO,GET_ERRORS,EXIT
    }
}
