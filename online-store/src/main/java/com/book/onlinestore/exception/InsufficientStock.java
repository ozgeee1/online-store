package com.book.onlinestore.exception;

public class InsufficientStock extends BookStoreException{
    public InsufficientStock(String message) {
        super(message);
    }

    public InsufficientStock(String message, Throwable cause) {
        super(message, cause);
    }
}
