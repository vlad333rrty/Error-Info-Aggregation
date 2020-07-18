package com.modules;

import com.data.Event;
import com.rest.ServerException;

public interface IInfoManager {
    void displayInfo(Event event) throws ServerException;
}
