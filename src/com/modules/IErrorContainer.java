package com.modules;

import com.data.Group;
import com.data.IError;

public interface IErrorContainer {
    void add(IError error);
    interface IAggregationManager {
        boolean isDuplicate(IError error);
        Group getGroup(IError error);
    }
}

