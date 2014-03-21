package com.engagepoint.university.admincentre.exception;

public class WrongInputArgException extends Exception {

    public WrongInputArgException() {
       super();
    }

    public WrongInputArgException(String message) {
        super(message);
    }

    public WrongInputArgException(String message, Throwable cause) {
        super(message, cause);
    }
}
