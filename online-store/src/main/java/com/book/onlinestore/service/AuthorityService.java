package com.book.onlinestore.service;

import com.book.onlinestore.entity.Authority;
import com.book.onlinestore.exception.AuthorityNotFoundException;
import com.book.onlinestore.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    /**
     *This method returns Authority based on given userId
     * If there is no Authority then throws AuthorityNotFoundException
     * @param userId
     * @return Authority
     */
    public Authority findAuthorityByUserId(Long userId){
        Optional<Authority> authorityByUserId = authorityRepository.findAuthorityByUserId(userId);
        return authorityByUserId.orElseThrow(() -> new AuthorityNotFoundException("Authority with user id: "+userId+" not found"));
    }
}
