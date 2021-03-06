package com.engagepoint.university.admincentre.exception;

public class WrongInputArgException extends Exception {

    public WrongInputArgException() {
        this("You enter wrong argument. Try to use -help to get information about "
                + "application commands");
    }

    public WrongInputArgException(String message) {
        super(message);
    }

    public WrongInputArgException(String message, Throwable cause) {
        super(message, cause);
    }
}
