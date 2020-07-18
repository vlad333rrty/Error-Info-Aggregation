package com.rest;

import com.data.Event;
import com.modules.IEventHandler;

import java.util.Arrays;
import java.util.Scanner;

public class EventHandler implements IEventHandler {
    @Override
    public Event getEvent() {
        Scanner scanner=new Scanner(System.in);
        Event e=null;

        while (e==null) {
            String s=scanner.nextLine().trim().toUpperCase();
            switch (s) {
                case "GET_INFO":
                    e = new Event(Event.Type.GET_INFO);
                    break;
                case "GET_ERRORS":
                    e = new Event(Event.Type.GET_ERRORS);
                    break;
                case "EXIT":
                    e = new Event(Event.Type.EXIT);
                    break;
                default:
                    System.out.println(String.format("Unknown command: %s\navailable commands: %s",s, Arrays.toString(Event.Type.values())));
            }
        }
        return e;
    }
}
