package com.book.onlinestore.exception;

public class MinimumPriceForOrderException extends BookStoreException{


    public MinimumPriceForOrderException(String message) {
        super(message);
    }

    public MinimumPriceForOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
