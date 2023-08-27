package com.book.onlinestore.controller;

import com.book.onlinestore.dto.request.BookSaveRequest;
import com.book.onlinestore.dto.request.BookUpdateRequest;
import com.book.onlinestore.dto.response.BookResponse;
import com.book.onlinestore.entity.Book;
import com.book.onlinestore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private BookController bookController;

    @MockBean
    private BookService bookService;

    private BookSaveRequest bookSaveRequest = new BookSaveRequest();
    private BookResponse bookResponse = new BookResponse();

    private Book book = new Book();

    @BeforeEach
    public void init() {
        bookSaveRequest.setPrice(BigDecimal.valueOf(12.99));
        bookSaveRequest.setTitle("Test Book");
        bookSaveRequest.setAuthor("Test Author");
        bookSaveRequest.setStockQuantity(10);

        bookResponse.setPrice(BigDecimal.valueOf(12.99));
        bookResponse.setTitle("Test Book");
        bookResponse.setAuthor("Test Author");
        bookResponse.setStockQuantity(10);

        book.setId(UUID.randomUUID());
        book.setPrice(BigDecimal.valueOf(12.99));
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setStockQuantity(10);


    }

    @Test
    public void Given_BookSaveRequest_When_AdminRole_SaveBook() throws Exception {

        when(bookService.saveBook(bookSaveRequest)).thenReturn(bookResponse);

        MvcResult result = mockMvc.perform(post("/books")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookSaveRequest)))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    public void Given_BookSaveRequest_When_UserRole_ThrowException()throws Exception {

        MvcResult result = mockMvc.perform(post("/books")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookSaveRequest)))
                .andExpect(status().isForbidden())
                .andReturn();

    }

    @Test
    public void Given_PageNumberAndSize_Then_GetBookResponseList() throws Exception {

        mockMvc.perform(get("/books")
                        .param("pageNumber", "0")
                        .param("pageSize", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Given_BookId_Then_GetBookResponse() throws Exception {
        UUID isbn = UUID.randomUUID();

        when(bookService.getBookById(isbn)).thenReturn(bookResponse);

        ResultActions result = mockMvc.perform(get("/books/{isbn}", isbn)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookResponse)));
    }


    @Test
    public void Given_BookId_When_AdminRole_Then_UpdateBook() throws Exception {
        UUID isbn = UUID.randomUUID();

        BookUpdateRequest updateRequest = new BookUpdateRequest();
        updateRequest.setPrice(BigDecimal.valueOf(34.99));
        updateRequest.setStockQuantity(40);
        updateRequest.setTitle("Updated Title");

        bookResponse.setPrice(BigDecimal.valueOf(34.99));
        bookResponse.setTitle("Updated Title");
        bookResponse.setStockQuantity(40);

        when(bookService.updateBook(isbn,updateRequest)).thenReturn(bookResponse);

        ResultActions result = mockMvc.perform(put("/books/{isbn}", isbn)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("admin").roles("ADMIN"))
                .content(objectMapper.writeValueAsString(updateRequest)));

        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookResponse)));
    }

    @Test
    public void Given_BookId_When_UserRole_Then_UpdateBook() throws Exception {
        UUID isbn = UUID.randomUUID();

        BookUpdateRequest updateRequest = new BookUpdateRequest();
        updateRequest.setPrice(BigDecimal.valueOf(34.99));
        updateRequest.setStockQuantity(40);
        updateRequest.setTitle("Updated Title");

        ResultActions result = mockMvc.perform(put("/books/{isbn}", isbn)
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("user").roles("USER"))
                .content(objectMapper.writeValueAsString(updateRequest)));

        result.andExpect(status().isForbidden());
    }


    @Test
    public void Given_BookId_When_AdminRole_Then_DeleteBook() throws Exception {
        UUID isbn = UUID.randomUUID();

        ResultActions result = mockMvc.perform(delete("/books/{isbn}", isbn)
                .with(user("admin").roles("ADMIN")));

        result.andExpect(status().isOk())
                .andExpect(content().string("Book is deleted successfully"));

        verify(bookService, times(1)).deleteBook(isbn);
    }

    @Test
    public void Given_BookId_When_UserRole_Then_DeleteBook() throws Exception {
        UUID isbn = UUID.randomUUID();

        ResultActions result = mockMvc.perform(delete("/books/{isbn}", isbn)
                .with(user("user").roles("USER")));

        result.andExpect(status().isForbidden());

        verifyNoInteractions(bookService);
    }

}