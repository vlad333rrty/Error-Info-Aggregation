package com.rest;

public class ServerException extends Throwable{
    private Exception exception;
    public ServerException(Exception exception){
        this.exception=exception;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String getMessage() {
        return exception.getMessage();
    }
}
