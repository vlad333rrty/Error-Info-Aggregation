package com.modules;

import com.data.IGroup;
import com.rest.ServerException;

import java.util.List;

public interface IErrorWriter {
    void write(List<IGroup> groups) throws ServerException;
}
