package com.data;

public class Event {
    private EventType type;
    public Event(EventType type){
        this.type=type;
    }

    public EventType getType() {
        return type;
    }

    public enum EventType{
        GET_INFO,
    }
}
