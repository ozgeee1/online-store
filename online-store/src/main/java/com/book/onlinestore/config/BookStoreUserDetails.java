package com.book.onlinestore.config;

import com.book.onlinestore.entity.Authority;
import com.book.onlinestore.entity.User;
import com.book.onlinestore.exception.UserNotFoundException;
import com.book.onlinestore.repository.AuthorityRepository;
import com.book.onlinestore.repository.UserRepository;
import com.book.onlinestore.service.AuthorityService;
import com.book.onlinestore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookStoreUserDetails implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityService authorityService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Optional<User> optionalUser = userRepository.findByName(username);
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException("User with name " + username + " not found"));

        Authority authority = authorityService.findAuthorityByUserId(user.getId());
        authorities.add(new SimpleGrantedAuthority("ROLE_"+authority.getRoleName()));
        return new org.springframework.security.core.userdetails.User(user.getName(),user.getPassword(),authorities);
    }
}
