package com.book.onlinestore.exception;

public class AuthorityNotFoundException extends BookStoreException{
    public AuthorityNotFoundException(String message) {
        super(message);
    }

    public AuthorityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
