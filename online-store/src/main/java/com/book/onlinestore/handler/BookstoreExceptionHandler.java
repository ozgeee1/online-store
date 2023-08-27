package com.book.onlinestore.handler;

import com.book.onlinestore.dto.ErrorDTO;
import com.book.onlinestore.exception.BookStoreException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Slf4j
@ControllerAdvice
public class BookstoreExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = { MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<ObjectError> allErrors = methodArgumentNotValidException.getBindingResult().getAllErrors();
        StringBuilder errorMessage = new StringBuilder("");
        for( ObjectError error : allErrors )
        {
            errorMessage.append(error.getDefaultMessage()).append("; ");
        }
        return ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessage.toString())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(value = { BookStoreException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(BookStoreException bookStoreException) {
        String message = bookStoreException.getMessage();
        return ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .build();
    }

    @ResponseBody
    @ExceptionHandler(value = { RequestNotPermitted.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(RequestNotPermitted requestNotPermitted) {
        String message = requestNotPermitted.getMessage();
        return ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .build();
    }

}
