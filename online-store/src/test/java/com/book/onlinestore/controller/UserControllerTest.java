package com.book.onlinestore.controller;

import com.book.onlinestore.dto.request.UserSignupRequest;
import com.book.onlinestore.jwt.JwtTokenUtil;
import com.book.onlinestore.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserController userController;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;



    @Test
    public void Given_UserSignupRequest_When_InvalidPassword_Then_CreateNewUser() throws Exception {
        String invalidPassword= "invaLidp";

        UserSignupRequest userSignupRequest = new UserSignupRequest();
        userSignupRequest.setName("testuser");
        userSignupRequest.setPassword(invalidPassword);


        ResultActions result = mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupRequest)));

        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Password must contain at least one number and one letter; "));
        verify(userService, times(0)).signupUser(userSignupRequest);
    }



    @Test
    public void Given_UserSignupRequest_When_ValidPassword_Then_CreateNewUser() throws Exception {
        String validPassword= "123456Aa";

        UserSignupRequest userSignupRequest = new UserSignupRequest();
        userSignupRequest.setName("testuser");
        userSignupRequest.setPassword(validPassword);


        ResultActions result = mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSignupRequest)));

        result.andExpect(status().isOk())
                .andExpect(content().string("New user is created: testuser"));

        verify(userService, times(1)).signupUser(userSignupRequest);
    }


}