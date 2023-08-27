package com.book.onlinestore.service;

import com.book.onlinestore.dto.BookItem;
import com.book.onlinestore.dto.request.BookSaveRequest;
import com.book.onlinestore.dto.request.BookUpdateRequest;
import com.book.onlinestore.dto.response.BookResponse;
import com.book.onlinestore.entity.Book;
import com.book.onlinestore.exception.BookNotFoundException;
import com.book.onlinestore.mapper.BookMapper;
import com.book.onlinestore.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    private BookSaveRequest  request = new BookSaveRequest();
    private Book bookEntity = new Book();

    private Book savedBook = new Book();;
    private BookResponse expectedResponse = new BookResponse();;

    private final static UUID BOOK_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");


    @Test
    void Given_BookSaveRequest_WhenTitleAndAuthorExists_Then_UpdateBookInDB() {

        request.setTitle("Test Title");
        request.setAuthor("Test Author");
        request.setPrice(BigDecimal.valueOf(12.99));
        request.setStockQuantity(10);

        bookEntity.setId(BOOK_ID);
        bookEntity.setTitle("Test Title");
        bookEntity.setAuthor("Test Author");
        bookEntity.setPrice(BigDecimal.valueOf(15.99));
        bookEntity.setStockQuantity(12);

        expectedResponse.setTitle("Test Title");
        expectedResponse.setAuthor("Test Author");
        expectedResponse.setPrice(BigDecimal.valueOf(12.99));
        expectedResponse.setStockQuantity(22);




        savedBook.setId(BOOK_ID);
        savedBook.setTitle("Test Title");
        savedBook.setAuthor("Test Author");
        savedBook.setPrice(BigDecimal.valueOf(12.99));
        savedBook.setStockQuantity(22);


        when(bookRepository.findByTitleAndAuthor(request.getTitle(),request.getAuthor())).thenReturn(Optional.of(bookEntity));

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        when(bookMapper.BookEntityToBookResponse(savedBook)).thenReturn(expectedResponse);

        BookResponse result = bookService.saveBook(request);

        verify(bookRepository, times(1)).findByTitleAndAuthor(request.getTitle(), request.getAuthor());
        verify(bookMapper, times(0)).BookRequestToBookEntity(request);
        verify(bookRepository, times(1)).save(bookEntity);
        verify(bookMapper, times(1)).BookEntityToBookResponse(bookEntity);

        Assertions.assertEquals(expectedResponse, result);

    }

    @Test
    void Given_BookSaveRequest_WhenTitleAndAuthorNotExists_Then_CreateNewBook() {

        bookEntity.setId(BOOK_ID);
        bookEntity.setTitle("Test Title");
        bookEntity.setAuthor("Test Author");
        bookEntity.setPrice(BigDecimal.valueOf(15.99));
        bookEntity.setStockQuantity(12);

        savedBook.setId(BOOK_ID);
        savedBook.setTitle("Test Title");
        savedBook.setAuthor("Test Author");
        savedBook.setPrice(BigDecimal.valueOf(15.99));
        savedBook.setStockQuantity(12);

        when(bookRepository.findByTitleAndAuthor(request.getTitle(),request.getAuthor())).thenReturn(Optional.empty());

        when(bookMapper.BookRequestToBookEntity(request)).thenReturn(bookEntity);

        when(bookRepository.save(any(Book.class))).thenReturn(bookEntity);

        when(bookMapper.BookEntityToBookResponse(savedBook)).thenReturn(expectedResponse);

        BookResponse result = bookService.saveBook(request);

        verify(bookRepository, times(1)).findByTitleAndAuthor(request.getTitle(), request.getAuthor());
        verify(bookMapper, times(1)).BookRequestToBookEntity(request);
        verify(bookRepository, times(1)).save(bookEntity);
        verify(bookMapper, times(1)).BookEntityToBookResponse(bookEntity);

        Assertions.assertEquals(expectedResponse, result);

    }


    @Test
    void Given_PageNumberAndSize_Then_GetOrdersSortedByCreatedAt() {
        int pageNumber = 0;
        int pageSize = 3;

        List<Book> mockBookList = new ArrayList<>();
        mockBookList.add(createMockBook("Book 1", "Author 1",LocalDateTime.of(2023,8,26,9,23)));
        mockBookList.add(createMockBook("Book 2", "Author 2",LocalDateTime.of(2023,8,26,9,22)));
        mockBookList.add(createMockBook("Book 3", "Author 3",LocalDateTime.of(2023,8,26,9,21)));


        when(bookRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(mockBookList);
        when(bookMapper.BookEntityToBookResponse(any(Book.class))).thenReturn(new BookResponse());

        List<BookResponse> result = bookService.getBooksByPagination(pageNumber, pageSize);

        verify(bookRepository, times(1)).findAllByOrderByCreatedAtDesc(any(Pageable.class));
        verify(bookMapper, times(mockBookList.size())).BookEntityToBookResponse(any(Book.class));

        Assertions.assertEquals(mockBookList.size(), result.size());

    }
    private Book createMockBook(String title, String author,LocalDateTime createdAt) {
        Book book = new Book();
        book.setId(UUID.randomUUID());
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(BigDecimal.valueOf(29.99));
        book.setStockQuantity(100);
        book.setCreatedAt(createdAt);
        return book;
    }

    @Test
    void Given_BookId_WhenValid_ThenGetBookResponse() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.BookEntityToBookResponse(bookEntity)).thenReturn(expectedResponse);
        BookResponse result = bookService.getBookById(BOOK_ID);

        verify(bookRepository, times(1)).findById(BOOK_ID);
        verify(bookMapper, times(1)).BookEntityToBookResponse(bookEntity);

        Assertions.assertNotNull(result);

    }


    @Test
    void Given_BookUpdateRequest_Then_UpdateBookInDB() {
        UUID bookId = UUID.randomUUID();
        BookUpdateRequest updateRequest = new BookUpdateRequest();
        updateRequest.setTitle("Updated Book Title");

        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Old Book Title");
        existingBook.setAuthor("John Doe");
        existingBook.setPrice(BigDecimal.valueOf(29.99));
        existingBook.setStockQuantity(100);

        Book updatedBook = new Book();
        updatedBook.setId(existingBook.getId());
        updatedBook.setTitle(updateRequest.getTitle());
        updatedBook.setAuthor(existingBook.getAuthor());
        updatedBook.setPrice(existingBook.getPrice());
        updatedBook.setStockQuantity(existingBook.getStockQuantity());

        BookResponse bookResponse = BookResponse.builder().title(updatedBook.getTitle()).author(updatedBook.getAuthor())
                .price(updatedBook.getPrice()).build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookMapper.updateBookEntityFromBookUpdateRequest(updateRequest, existingBook)).thenReturn(updatedBook);
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.BookEntityToBookResponse(updatedBook)).thenReturn(bookResponse);


        BookResponse result = bookService.updateBook(bookId, updateRequest);

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapper, times(1)).updateBookEntityFromBookUpdateRequest(updateRequest, existingBook);
        verify(bookRepository, times(1)).save(updatedBook);
        verify(bookMapper, times(1)).BookEntityToBookResponse(updatedBook);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(updateRequest.getTitle(), result.getTitle());


    }

    @Test
    void Given_BookId_Then_DeleteBookEntity() {
        Book bookToBeDeleted = new Book();
        bookToBeDeleted.setId(BOOK_ID);

        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(bookToBeDeleted));

        bookService.deleteBook(BOOK_ID);

        verify(bookRepository, times(1)).findById(BOOK_ID);
        verify(bookRepository, times(1)).delete(bookToBeDeleted);


    }

    @Test
    void Given_BookId_WhenValid_GetBookEntity() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(bookEntity));

        Book result = bookService.getBookByIdOrElseThrowException(BOOK_ID);

        verify(bookRepository, times(1)).findById(BOOK_ID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookEntity.getId(), result.getId());

    }

    @Test
    void Given_BookId_WhenNotValid_Then_ThrowsBookNotFoundException() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            bookService.getBookByIdOrElseThrowException(BOOK_ID);
        });
        String expectedMessage = "Book with id: "+ BOOK_ID.toString()+ " not found";
        String actualMessage = exception.getMessage();

        verify(bookRepository, times(1)).findById(BOOK_ID);

        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void getBookListFromBookItemListAndTotalPrice() {
        UUID bookId1 = UUID.randomUUID();
        UUID bookId2 = UUID.randomUUID();

        BookItem bookItem1 = new BookItem();
        bookItem1.setQuantity(2);
        bookItem1.setBookId(bookId1);

        BookItem bookItem2 = new BookItem();
        bookItem2.setQuantity(3);
        bookItem2.setBookId(bookId2);

        List<BookItem> bookItems = new ArrayList<>();
        bookItems.add(bookItem1);
        bookItems.add(bookItem2);

        Book mockBook1 = new Book();
        mockBook1.setId(bookId1);
        mockBook1.setTitle("Book 1");
        mockBook1.setPrice(BigDecimal.valueOf(19.99));

        Book mockBook2 = new Book();
        mockBook2.setId(bookId2);
        mockBook2.setTitle("Book 2");
        mockBook2.setPrice(BigDecimal.valueOf(24.99));

        BigDecimal calculatedResult = mockBook1.getPrice().multiply(BigDecimal.valueOf(bookItem1.getQuantity())).add(mockBook2.getPrice().multiply(BigDecimal.valueOf(bookItem2.getQuantity())));

        when(bookRepository.findById(bookId1)).thenReturn(Optional.of(mockBook1));
        when(bookRepository.findById(bookId2)).thenReturn(Optional.of(mockBook2));

        HashMap<BigDecimal, List<Book>> result = bookService.getBookListFromBookItemListAndTotalPrice(bookItems);
        verify(bookRepository, times(2)).findById(any(UUID.class));

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.containsKey(calculatedResult));
        Assertions.assertEquals(2, result.get(calculatedResult).size());
    }



    @Test
    void fixStockQuantity() {
        Book existingBook = new Book();
        existingBook.setId(BOOK_ID);
        existingBook.setTitle("Sample Book");
        existingBook.setStockQuantity(10);

        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(existingBook));

        int quantityToFix = 3;

        bookService.fixStockQuantity(BOOK_ID, quantityToFix);

        verify(bookRepository, times(1)).findById(BOOK_ID);
        verify(bookRepository, times(1)).save(existingBook);

        Assertions.assertEquals(7, existingBook.getStockQuantity());


    }
}