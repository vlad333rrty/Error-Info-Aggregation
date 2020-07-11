package com.data;

import java.util.ArrayList;

public class Group {
    private int duplicatesNumber;
    private final ArrayList<IError> entry=new ArrayList<>();

    public void add(IError error){
        entry.add(error);
    }

    public int getDuplicatesNumber() {
        return duplicatesNumber;
    }
}
