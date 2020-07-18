package com.data;

public interface IError {
    String[] getFields();
    String getFieldValue(String fieldName);
    boolean isDuplicate(IError error);
}
