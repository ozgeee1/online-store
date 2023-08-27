package com.book.onlinestore.controller;

import com.book.onlinestore.dto.request.BookSaveRequest;
import com.book.onlinestore.dto.request.BookUpdateRequest;
import com.book.onlinestore.dto.response.BookResponse;
import com.book.onlinestore.service.BookService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("books")
public class BookController {

    private final BookService bookService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(produces = "application/json")
    @RateLimiter(name = "rateLimitingAPI")
    public ResponseEntity<BookResponse> saveBook(@RequestBody @Valid BookSaveRequest bookSaveRequest){
        BookResponse bookResponse = bookService.saveBook(bookSaveRequest);
        log.info("Book saved to db {}",bookResponse.toString());
        return ResponseEntity.ok(bookResponse);
    }


    @GetMapping
    @RateLimiter(name = "rateLimitingAPI")
    public ResponseEntity<List<BookResponse>> getBooksByPagination(@RequestParam(defaultValue = "0") int pageNumber,@RequestParam(defaultValue = "3") int pageSize){
        log.info("Books are retrieving with pagination");
        return ResponseEntity.ok(bookService.getBooksByPagination(pageNumber,pageSize));
    }

    @GetMapping("/{isbn}")
    @RateLimiter(name = "rateLimitingAPI")
    public ResponseEntity<BookResponse> getBookById(@PathVariable UUID isbn){
        log.info("Book with id: {} is getting from db",isbn.toString());
        return ResponseEntity.ok(bookService.getBookById(isbn));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{isbn}")
    @RateLimiter(name = "rateLimitingAPI")
    public ResponseEntity<BookResponse> updateBook(@PathVariable UUID isbn,@RequestBody BookUpdateRequest bookUpdateRequest){
        log.info("Book is updating with id: {}",isbn.toString());
        return ResponseEntity.ok(bookService.updateBook(isbn,bookUpdateRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{isbn}")
    @RateLimiter(name = "rateLimitingAPI")
    public ResponseEntity<?> deleteBook(@PathVariable UUID isbn){
        log.info("Book is deleting with id: {}",isbn.toString());
        bookService.deleteBook(isbn);
        return  ResponseEntity.ok("Book is deleted successfully");
    }

}
