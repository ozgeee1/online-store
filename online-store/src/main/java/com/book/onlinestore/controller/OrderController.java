package com.book.onlinestore.controller;

import com.book.onlinestore.dto.request.OrderRequest;
import com.book.onlinestore.dto.response.OrderResponse;
import com.book.onlinestore.service.OrderService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @RateLimiter(name = "rateLimitingAPI")
    public ResponseEntity<String> createOrder(@RequestBody @Valid OrderRequest orderRequest){
        orderService.createOrder(orderRequest);
        return ResponseEntity.ok("Order is created");
    }

    @GetMapping("/{userId}")
    @RateLimiter(name = "rateLimitingAPI")
    public ResponseEntity<List<OrderResponse>> getOrdersForUser(@PathVariable Long userId){
        return ResponseEntity.ok(orderService.getAllOrdersForUser(userId));
    }

    @GetMapping("/details/{orderId}")
    @RateLimiter(name = "rateLimitingAPI")
    public ResponseEntity<OrderResponse> getOrderAndDetails(@PathVariable Long orderId){
            return ResponseEntity.ok(orderService.getAllOrdersAndDetailsById(orderId));
    }

}
