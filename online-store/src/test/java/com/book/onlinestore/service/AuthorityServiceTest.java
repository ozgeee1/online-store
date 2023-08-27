package com.book.onlinestore.service;

import com.book.onlinestore.entity.Authority;
import com.book.onlinestore.entity.User;
import com.book.onlinestore.exception.AuthorityNotFoundException;
import com.book.onlinestore.repository.AuthorityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorityServiceTest {


    @InjectMocks
    private AuthorityService authorityService;

    @Mock
    private  AuthorityRepository authorityRepository;

    private Authority authority;


    @BeforeEach
    public void init() {
        authority = new Authority();
        authority.setId(1L);
        authority.setRoleName("USER");
        authority.setUser(new User());

    }

    @Test
    void Given_UserId_When_UserIdWithAuthorityExists_Then_Get_Authority() {
        Long userId = 1L;
        when(authorityRepository.findAuthorityByUserId(userId)).thenReturn(Optional.of(authority));

        Authority resultAuthority = authorityService.findAuthorityByUserId(userId);

        verify(authorityRepository, times(1)).findAuthorityByUserId(userId);

        assertEquals(authority, resultAuthority);

    }

    @Test
    void Given_UserId_When_UserIdWithAuthorityNotExists_Then_ThrowsAuthorityNotFoundException() {
        Long userId = 2L;
        when(authorityRepository.findAuthorityByUserId(userId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(AuthorityNotFoundException.class, () -> {
            authorityService.findAuthorityByUserId(userId);
        });
        String expectedMessage = "Authority with user id: "+userId+" not found";
        String actualMessage = exception.getMessage();

        verify(authorityRepository, times(1)).findAuthorityByUserId(userId);

        Assertions.assertTrue(actualMessage.contains(expectedMessage));


    }
}