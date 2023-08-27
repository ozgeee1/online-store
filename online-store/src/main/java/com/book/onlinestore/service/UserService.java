package com.book.onlinestore.service;

import com.book.onlinestore.config.BookStoreUserDetails;
import com.book.onlinestore.dto.request.UserLoginRequest;
import com.book.onlinestore.dto.request.UserSignupRequest;
import com.book.onlinestore.entity.Authority;
import com.book.onlinestore.entity.User;
import com.book.onlinestore.exception.UserNotFoundException;
import com.book.onlinestore.jwt.JwtTokenUtil;
import com.book.onlinestore.mapper.UserMapper;
import com.book.onlinestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private final BookStoreUserDetails userDetails;
    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    /**
     * This method creates new user with user role
     * If password is not valid then returns MethodArgumentNotValidException
     * @param userSignupRequest
     * @return ResponseEntity<String>
     */
    public ResponseEntity<String> signupUser(UserSignupRequest userSignupRequest){
        User user = userMapper.UserSignupRequestToUser(userSignupRequest);
        Authority authority = Authority.builder().user(user).roleName("USER").build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthority(authority);
        User newUser = userRepository.save(user);
        return ResponseEntity.ok("New user is created: "+newUser.getName());
    }

    /**
     * This method returns User for given username
     * If there is no user then throws UserNotFoundException
     * @param username
     * @return
     */
    public User findUserByName(String username){
        Optional<User> byName = userRepository.findByName(username);
        return byName.orElseThrow(()->new UserNotFoundException("User with name " + username + " not found"));
    }

    /**
     * This method returns token if user credentials are valid
     * If not then returns exception
     * @param userLoginRequest
     * @return ResponseEntity<?>
     */
    public ResponseEntity<?> loginUser(UserLoginRequest userLoginRequest){
        try {
            authenticate(userLoginRequest.getName(),userLoginRequest.getPassword());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        UserDetails uDetails = userDetails.loadUserByUsername(userLoginRequest.getName());
        final String token = jwtTokenUtil.generateToken(uDetails);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);

    }

    /**
     * This method authenticate user
     * If credentials are not valid then throws BadCredentialsException
     * @param username
     * @param password
     * @throws Exception
     */
    private void authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
