package com.modules;

import com.data.IGroup;
import com.data.IError;
import com.rest.ServerException;

import java.util.List;

public interface IErrorContainer {
    void add(IError error) throws ServerException;
    List<IGroup> retrieveGroups();
    boolean shouldBeUnloaded();
    interface IAggregationManager {
        IGroup getGroup(IError error);
    }
}

