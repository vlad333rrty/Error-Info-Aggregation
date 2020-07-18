package com.modules;

import com.data.IError;
import com.rest.ServerException;

public interface IConnectionHandler {
    IError getError();
    void setResult(IError error, ServerException se);
    void close();
    void start();
}
