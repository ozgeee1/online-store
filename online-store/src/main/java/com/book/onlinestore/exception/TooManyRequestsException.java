package com.book.onlinestore.exception;

public class TooManyRequestsException extends Exception {

    public TooManyRequestsException() {
        super("Too many requests");
    }
}
