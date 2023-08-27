package com.book.onlinestore.dto.request;

import com.book.onlinestore.dto.BookItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {


    @NotNull(message = "There should be at least one book for order")
    private List<@Valid BookItem> books;
}
