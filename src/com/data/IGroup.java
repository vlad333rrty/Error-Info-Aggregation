package com.data;

public interface IGroup extends Iterable<IError> {
    void add(IError error);
    void add(IError error,int n);
    IGroup merge(IGroup group);
    int getDuplicatesNumber(IError error);
    String getGroupingFieldValue();
    boolean fits(IError error);
    int size();
}
