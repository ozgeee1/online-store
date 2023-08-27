package com.book.onlinestore.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSaveRequest {

    @NotBlank(message = "Title can not be null")
    private String title;

    @NotBlank(message = "Author can not be null")
    private String author;

    @NotNull(message = "Price can not be null")
    @DecimalMin(value = "0.1",message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity can not be null")
    @Min(value = 1,message = "Stock quantity must be greater than 0")
    private int stockQuantity;


}
