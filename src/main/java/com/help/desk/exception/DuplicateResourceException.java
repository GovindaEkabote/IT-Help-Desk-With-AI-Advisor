package com.help.desk.exception;

public class DuplicateResourceException extends  RuntimeException{

    public DuplicateResourceException(String message) {
        super(message);
    }
}
