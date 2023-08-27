package com.book.onlinestore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Data
public class BookItem {

    @NotNull
    @Min(value = 1,message = "At least 1 book should be bought")
    private int quantity;

    @NotNull(message = "bookId can not be null")
    private UUID bookId;
}
