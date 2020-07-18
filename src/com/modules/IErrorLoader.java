package com.modules;

import com.data.IGroup;
import com.rest.ServerException;

import java.util.List;

public interface IErrorLoader {
    List<IGroup> load() throws ServerException;
    List<IGroup> load(List<IGroup> groups) throws ServerException;
}
