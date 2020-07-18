package com.rest;

import com.data.IGroup;
import com.data.IError;
import com.modules.IErrorContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class ErrorContainer implements IErrorContainer {
    private Queue<IGroup> entry=new LinkedBlockingDeque<>();
    private final IAggregationManager aggregator=new Aggregator();
    private int size;
    private static final int THRESHOLD=1;
    @Override
    public void add(IError error) throws ServerException {
        IGroup g=aggregator.getGroup(error);
        if (g==null){
            g=new GroupByName(error.getFieldValue("name"));
            entry.add(g);
        }
        size-=g.size();
        g.add(error);
        size+=g.size();
    }

    @Override
    public List<IGroup> retrieveGroups() {
        List<IGroup> res = new ArrayList<>(entry);
        entry=new LinkedBlockingDeque<>();
        size=0;
        return res;
    }

    @Override
    public boolean shouldBeUnloaded() {
        return size>THRESHOLD;
    }

    public class Aggregator implements IAggregationManager{
        @Override
        public IGroup getGroup(IError error) {
            for (IGroup g : entry) {
                if (g.fits(error)) return g;
            }
            return null;
        }
    }
}
