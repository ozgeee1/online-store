package com.book.onlinestore.mapper;

import com.book.onlinestore.dto.request.OrderRequest;
import com.book.onlinestore.dto.response.BookResponse;
import com.book.onlinestore.dto.response.OrderResponse;
import com.book.onlinestore.entity.Book;
import com.book.onlinestore.entity.Order;
import com.book.onlinestore.entity.User;
import com.book.onlinestore.service.BookService;
import com.book.onlinestore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public  class OrderMapper {

    private final BookService bookService;
    private final UserService userService;

    private final BookMapper bookMapper;

    public Order orderRequestToOrder(OrderRequest orderRequest){
        User user = null;
        HashMap<BigDecimal, List<Book>> resultMap = bookService.getBookListFromBookItemListAndTotalPrice(
                orderRequest.getBooks());
        BigDecimal totalPrice = resultMap.keySet().iterator().next();
        List<Book> bookList = resultMap.get(totalPrice);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            user = userService.findUserByName(userDetails.getUsername());
        }


        return Order.builder().books(bookList).user(user).totalPrice(totalPrice).build();
    }

    public OrderResponse orderToOrderResponse(Order order){
        List<BookResponse> bookResponseList = order.getBooks().stream().map(bookMapper::BookEntityToBookResponse).toList();
        return OrderResponse.builder().orderDate(order.getOrderDate())
                .createdAt(order.getCreatedAt())
                .books(bookResponseList)
                .updatedAt(order.getUpdatedAt())
                .totalPrice(order.getTotalPrice()).build();

    }
}
