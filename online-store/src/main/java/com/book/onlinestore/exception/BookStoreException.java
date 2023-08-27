package com.book.onlinestore.exception;

public class BookStoreException extends RuntimeException{

    public BookStoreException(String message) {
        super(message);
    }

    public BookStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
