package com.book.onlinestore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserSignupRequest {

    @NotBlank(message = "name can not be blank")
    private String name;
    @NotBlank(message = "password can not be blank")
    @Size(min = 8,max = 8,message = "Password must be 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$", message = "Password must contain both uppercase and lowercase letters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$", message = "Password must contain at least one number and one letter")
    private String password;

}
