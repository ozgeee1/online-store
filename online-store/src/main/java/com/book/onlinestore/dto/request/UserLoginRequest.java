package com.book.onlinestore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotBlank(message = "name can not be blank")
    private String name;
    @NotBlank(message = "password can not be blank")
    private String password;
}
