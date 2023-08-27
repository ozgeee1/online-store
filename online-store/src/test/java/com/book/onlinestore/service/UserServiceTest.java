package com.book.onlinestore.service;

import com.book.onlinestore.config.BookStoreUserDetails;
import com.book.onlinestore.dto.request.UserLoginRequest;
import com.book.onlinestore.dto.request.UserSignupRequest;
import com.book.onlinestore.entity.User;
import com.book.onlinestore.exception.UserNotFoundException;
import com.book.onlinestore.jwt.JwtTokenUtil;
import com.book.onlinestore.mapper.UserMapper;
import com.book.onlinestore.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;


    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private BookStoreUserDetails userDetails;

    @Mock
    private JwtTokenUtil jwtTokenUtil;




    @Test
    void Given_SignupRequest_Then_CreateNewUser() {
        UserSignupRequest signupRequest = new UserSignupRequest();
        signupRequest.setName("John Doe");
        signupRequest.setPassword("password");

        User mockUser = new User();
        mockUser.setPassword("password");
        mockUser.setName("John Doe");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John Doe");

        when(userMapper.UserSignupRequestToUser(signupRequest)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        ResponseEntity<?> result = userService.signupUser(signupRequest);

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password");

        assertNotNull(result);
        assertEquals(ResponseEntity.ok("New user is created: " + savedUser.getName()), result);
    }

    @Test
    void Given_UserName_WhenValid_Then_GetUser() {
        String name = "John Doe";

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName(name);

        when(userRepository.findByName(name)).thenReturn(Optional.of(mockUser));
        User result = userService.findUserByName(name);

        verify(userRepository, times(1)).findByName(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
    }

    @Test
    void Given_UserName_WhenNotValid_Then_ThrowsUserNotFoundException() {
        String name = "John Doe";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName(name);

        when(userRepository.findByName(name)).thenReturn(Optional.empty());
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.findUserByName(name);
        });
        String expectedMessage ="User with name " + name + " not found";
        String actualMessage = exception.getMessage();

        verify(userRepository, times(1)).findByName(name);

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void Given_UserLoginRequest_Then_LoginUser() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setName("john");
        loginRequest.setPassword("password");

        Map<String, String> response = new HashMap<>();
        response.put("token", "created token for user");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetails.loadUserByUsername("john")).thenReturn(new org.springframework.security.core.userdetails.User(
                "john", "password", new ArrayList<>()
        ));
        when(jwtTokenUtil.generateToken(any(UserDetails.class))).thenReturn("created token for user");
        ResponseEntity<?> result = userService.loginUser(loginRequest);



        assertNotNull(result);
        assertEquals(ResponseEntity.ok(response), result);
    }

    @Test
    void Given_UserLoginRequest_When_WrongCredentials_Then_ThrowsBadCredentialsException() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setName("john");
        loginRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("INVALID_CREDENTIALS"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser(loginRequest);
        });

    }

}