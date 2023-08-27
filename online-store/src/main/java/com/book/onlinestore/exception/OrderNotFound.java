package com.book.onlinestore.exception;

public class OrderNotFound extends BookStoreException{
    public OrderNotFound(String message) {
        super(message);
    }

    public OrderNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
