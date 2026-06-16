package com.help.desk.exception;

public class TicketAlreadyAssignedException extends RuntimeException{

    public TicketAlreadyAssignedException(String message) {
        super(message);
    }
}
