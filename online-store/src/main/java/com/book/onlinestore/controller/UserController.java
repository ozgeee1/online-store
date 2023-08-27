package com.book.onlinestore.controller;

import com.book.onlinestore.dto.request.UserLoginRequest;
import com.book.onlinestore.dto.request.UserSignupRequest;
import com.book.onlinestore.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @RateLimiter(name = "rateLimitingAPI")
    public ResponseEntity<String> signupUser(@Valid @RequestBody UserSignupRequest userSignupRequest){
        return userService.signupUser(userSignupRequest);
    }

    @PostMapping("/login")
    @RateLimiter(name = "rateLimitingAPI")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginRequest userLoginRequest){
        return userService.loginUser(userLoginRequest);
    }

}
