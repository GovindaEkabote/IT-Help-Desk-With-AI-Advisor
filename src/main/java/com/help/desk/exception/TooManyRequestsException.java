package com.help.desk.exception;

public class TooManyRequestsException  extends RuntimeException{
    public TooManyRequestsException(String message) {
        super(message);
    }
}
