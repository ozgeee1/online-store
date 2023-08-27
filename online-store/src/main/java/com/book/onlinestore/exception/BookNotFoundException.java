package com.book.onlinestore.exception;

public class BookNotFoundException extends BookStoreException{

    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
