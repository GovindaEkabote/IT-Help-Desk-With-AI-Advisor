package com.help.desk.exception;

public class TicketAlreadyClosedException extends RuntimeException{

    public TicketAlreadyClosedException(String message) {
        super(message);
    }
}
