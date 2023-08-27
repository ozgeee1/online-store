package com.book.onlinestore.controller;

import com.book.onlinestore.dto.BookItem;
import com.book.onlinestore.dto.request.OrderRequest;
import com.book.onlinestore.dto.response.BookResponse;
import com.book.onlinestore.dto.response.OrderResponse;
import com.book.onlinestore.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class OrderControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderController orderController;

    @Test
    public void Given_OrderRequest_When_AuthorizedUser_Then_CreateOrder() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        BookItem bookItem1= new BookItem();
        bookItem1.setBookId(UUID.randomUUID());
        bookItem1.setQuantity(2);

        BookItem bookItem2= new BookItem();
        bookItem2.setBookId(UUID.randomUUID());
        bookItem2.setQuantity(1);

        orderRequest.setBooks(List.of(bookItem1,bookItem2));

        mockMvc.perform(post("/orders").with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order is created"));

        verify(orderService, times(1)).createOrder(orderRequest);
    }

    @Test
    public void Given_OrderRequest_When_UnauthorizedUser_Then_CreateOrder() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        BookItem bookItem1= new BookItem();
        bookItem1.setBookId(UUID.randomUUID());
        bookItem1.setQuantity(2);

        BookItem bookItem2= new BookItem();
        bookItem1.setBookId(UUID.randomUUID());
        bookItem1.setQuantity(1);

        orderRequest.setBooks(List.of(bookItem1,bookItem2));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderRequest)))
                .andExpect(status().isUnauthorized());

        verify(orderService, times(0)).createOrder(orderRequest);
    }


    @Test
    public void Given_InvalidOrderRequest_Then_ThrowsExceptionForBookIdValidation() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        BookItem bookItem1= new BookItem();
        bookItem1.setQuantity(2);

        orderRequest.setBooks(List.of(bookItem1));

        mockMvc.perform(post("/orders").with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("bookId can not be null; "));

        verify(orderService, times(0)).createOrder(orderRequest);
    }

    @Test
    public void Given_InvalidOrderRequest_Then_ThrowsExceptionForQuantityValidation() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        BookItem bookItem1= new BookItem();
        bookItem1.setBookId(UUID.randomUUID());

        orderRequest.setBooks(List.of(bookItem1));

        mockMvc.perform(post("/orders").with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("At least 1 book should be bought; "));

        verify(orderService, times(0)).createOrder(orderRequest);
    }


    @Test
    public void Given_UserId_Then_Get_CorrespondingOrders() throws Exception {
        Long userId = 123L;
        OrderResponse res1 = OrderResponse.builder().totalPrice(BigDecimal.valueOf(36.99)).build();
        OrderResponse res2 = OrderResponse.builder().totalPrice(BigDecimal.valueOf(47.99)).build();
        List<OrderResponse> orderResponses = List.of(res1,res2);

        when(orderService.getAllOrdersForUser(userId)).thenReturn(orderResponses);

        mockMvc.perform(get("/orders/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(orderResponses.size())));

        verify(orderService, times(1)).getAllOrdersForUser(userId);
    }

    @Test
    public void Given_OrderId_Then_Get_CorrespondingOrderDetails() throws Exception {
        Long orderId = 456L;
        OrderResponse orderResponse = OrderResponse.builder()
                .totalPrice(BigDecimal.valueOf(36.99))
                .books(List.of(new BookResponse()))
                .build();

        when(orderService.getAllOrdersAndDetailsById(orderId)).thenReturn(orderResponse);

        mockMvc.perform(get("/orders/details/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(orderService, times(1)).getAllOrdersAndDetailsById(orderId);
    }
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}