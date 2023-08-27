package com.book.onlinestore.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookUpdateRequest {

    private String title;
    private String author;
    private BigDecimal price;
    private int stockQuantity;
}
