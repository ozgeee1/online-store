package com.book.onlinestore.service;

import com.book.onlinestore.dto.BookItem;
import com.book.onlinestore.dto.request.OrderRequest;
import com.book.onlinestore.dto.response.OrderResponse;
import com.book.onlinestore.entity.Authority;
import com.book.onlinestore.entity.Book;
import com.book.onlinestore.entity.Order;
import com.book.onlinestore.entity.User;
import com.book.onlinestore.mapper.OrderMapper;
import com.book.onlinestore.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private BookService bookService;

    @Mock
    private OrderMapper orderMapper;


    private Book mockBook1 = new Book();
    private Book mockBook2 = new Book();


    private final UUID bookId1 = UUID.randomUUID();
    private final UUID bookId2 = UUID.randomUUID();

    private OrderRequest orderRequest = new OrderRequest();
    private Order order;
    private Order order2;
    private User user;

    private OrderResponse response1 ;

    @BeforeEach
    public void init() {


        BookItem bookItem1 = new BookItem();
        bookItem1.setQuantity(2);
        bookItem1.setBookId(bookId1);

        BookItem bookItem2 = new BookItem();
        bookItem2.setQuantity(3);
        bookItem2.setBookId(bookId2);

        List<BookItem> bookItems = new ArrayList<>();
        bookItems.add(bookItem1);
        bookItems.add(bookItem2);

        orderRequest.setBooks(bookItems);

        mockBook1.setId(bookId1);
        mockBook1.setTitle("Book 1");
        mockBook1.setStockQuantity(5);
        mockBook1.setPrice(BigDecimal.valueOf(7.99));

        mockBook2.setId(bookId2);
        mockBook2.setTitle("Book 2");
        mockBook2.setStockQuantity(10);
        mockBook2.setPrice(BigDecimal.valueOf(12.99));

        user = User.builder()
                .name("Test User")
                .id(1L).build();

        order = Order.builder().id(1L)
                .books(List.of(mockBook1,mockBook2))
                .totalPrice(mockBook1.getPrice().add(mockBook2.getPrice())).build();
        user.setOrders(Set.of(order));
        order.setUser(user);

        order2 = Order.builder().id(2L)
                .books(List.of(mockBook1,mockBook2))
                .totalPrice(mockBook1.getPrice().add(mockBook2.getPrice())).build();
        user.setOrders(Set.of(order,order2));
        order.setUser(user);
        order2.setUser(user);

        response1 = OrderResponse.builder().totalPrice(order.getTotalPrice()).build();

    }



    @Test
    void Given_OrderRequest_WhenValid_Then_CreateOrder() {

        when(bookService.getBookByIdOrElseThrowException(bookId1)).thenReturn(mockBook1);
        when(bookService.getBookByIdOrElseThrowException(bookId2)).thenReturn(mockBook2);
        when(orderMapper.orderRequestToOrder(any(OrderRequest.class))).thenReturn(order);

        orderService.createOrder(orderRequest);

        verify(bookService, times(4)).getBookByIdOrElseThrowException(any(UUID.class));
        verify(orderRepository, times(1)).save(any(Order.class));

    }

    @Test
    void Given_UserId_Then_GetAllOrders() {

        when(orderRepository.findByUserIdOrderByUpdatedAtDesc(user.getId())).thenReturn(List.of(order,order2));

        List<OrderResponse> result = orderService.getAllOrdersForUser(user.getId());

        verify(orderRepository, times(1)).findByUserIdOrderByUpdatedAtDesc(user.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void Given_OrderId_Then_Get_AllOrderDetails() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.orderToOrderResponse(order)).thenReturn(response1);
        OrderResponse result = orderService.getAllOrdersAndDetailsById(1L);

        verify(orderRepository, times(1)).findById(1L);
        assertNotNull(result);

        assertEquals(order.getTotalPrice(), result.getTotalPrice());
    }
}