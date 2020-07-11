package com.rest;

import com.data.Error;
import com.data.Group;
import com.data.IError;
import com.modules.IErrorContainer;

import java.util.ArrayList;

public class ErrorContainer implements IErrorContainer {
    private final ArrayList<Group> entry=new ArrayList<>();
    private final IAggregationManager aggregator=new Aggregator();

    @Override
    public void add(IError error) {
        Group g=aggregator.getGroup(error);
        if (g==null){
            g=new Group();
            entry.add(g);
        }
        g.add(error);
    }

    public class Aggregator implements IAggregationManager{

        @Override
        public boolean isDuplicate(IError error) {
            return false;
        }

        @Override
        public Group getGroup(IError error) {
            if (error instanceof Error){
                return entry.size()!=0 ? entry.get(0) : null;
            }else{
                throw new IllegalArgumentException("Unknown ERROR TYPE");
            }
        }
    }

}
