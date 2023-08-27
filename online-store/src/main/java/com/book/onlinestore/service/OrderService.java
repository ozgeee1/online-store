package com.book.onlinestore.service;

import com.book.onlinestore.dto.BookItem;
import com.book.onlinestore.dto.request.OrderRequest;
import com.book.onlinestore.dto.response.OrderResponse;
import com.book.onlinestore.entity.Order;
import com.book.onlinestore.exception.InsufficientStock;
import com.book.onlinestore.exception.MinimumPriceForOrderException;
import com.book.onlinestore.exception.OrderNotFound;
import com.book.onlinestore.mapper.OrderMapper;
import com.book.onlinestore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookService bookService;
    private final OrderMapper orderMapper;

    /**
     * This method creates new order if every field of OrderRequest is valid
     * then updates book's stocks
     * @param orderRequest
     */
    @Transactional
    public void createOrder(OrderRequest orderRequest){
        validateOrder(orderRequest);
        Order order = orderMapper.orderRequestToOrder(orderRequest);
        orderRepository.save(order);
        decreaseBookQuantitiesFromStock(orderRequest.getBooks());
    }

    /**
     * This method calls fixStockQuantity for every bookItem in the bookItems
     * @param bookItems
     */
    private void decreaseBookQuantitiesFromStock(List<BookItem> bookItems) {
        bookItems.forEach(bookItem -> bookService.fixStockQuantity(bookItem.getBookId(),bookItem.getQuantity()));
    }

    /**
     * This method checks rules before creating new order
     * @param orderRequest
     */
    private void validateOrder(OrderRequest orderRequest){
            validateStockQuantity(orderRequest);
            validatePrice(orderRequest);
    }

    /**
     * This method checks if order's total price is bigger than 25$ or not
     * @param orderRequest
     */
    private void validatePrice(OrderRequest orderRequest) {
        BigDecimal totalPrice = orderRequest.getBooks().stream()
                .map(bookItem -> calculatePrice(bookService.getBookByIdOrElseThrowException(bookItem.getBookId()).getPrice(), bookItem.getQuantity()))
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
        if(totalPrice.compareTo(BigDecimal.valueOf(25)) < 0){
            throw new MinimumPriceForOrderException("Minimum price for order is 25$");
        }
    }

    /**
     * This method returns Order record for given id
     * If there is no record then throws OrderNotFoundException
     * @param id
     * @return Order
     */
    private Order getOrderById(Long id){
        Optional<Order> orderById = orderRepository.findById(id);
        return orderById.orElseThrow(()->new OrderNotFound("Order with id " + id + " not found"));
    }

    /**
     * This method calculates total price for given price and quantity
     * @param price
     * @param quantity
     * @return
     */
    private BigDecimal calculatePrice(BigDecimal price,int quantity){
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * This method checks if there is enough book quantity for order
     * @param orderRequest
     */
    private void validateStockQuantity(OrderRequest orderRequest) {
        orderRequest.getBooks().forEach(bookItem -> {
            if(bookItem.getQuantity()>bookService.getBookByIdOrElseThrowException(bookItem.getBookId()).getStockQuantity()){
                throw new InsufficientStock("Requested quantity "+bookItem.getQuantity()+" is more than exists");
            }
        });
    }

    /**
     * This method returns all Orders for given userId
     * If there is no Order, then returns empty list
     * @param userId
     * @return List<OrderResponse>
     */
    public List<OrderResponse> getAllOrdersForUser(Long userId){
        List<Order> byUserIdOrderByUpdateDateDesc = orderRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return byUserIdOrderByUpdateDateDesc.stream().map(orderMapper::orderToOrderResponse).collect(Collectors.toList());

    }

    /**
     * This method returns Order details for given orderId
     * If there is no record, then throws OrderNotFoundException
     * @param orderId
     * @return OrderResponse
     */
    public OrderResponse getAllOrdersAndDetailsById(Long orderId){
        Order order = getOrderById(orderId);
        return orderMapper.orderToOrderResponse(order);
    }


}
