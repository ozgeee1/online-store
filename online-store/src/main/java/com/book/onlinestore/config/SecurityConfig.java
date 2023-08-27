package com.book.onlinestore.config;

import com.book.onlinestore.jwt.JwtTokenValidatorFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.OutputStream;
import java.nio.file.AccessDeniedException;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenValidatorFilter jwtTokenValidatorFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public AuthenticationEntryPoint authenticationEntryPoint(){
        return ((request, response, authException) -> {
            System.out.println("AuthenticationEntryPoint -> AuthenticationException occured!");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            OutputStream out = response.getOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            if (authException instanceof BadCredentialsException) {
                mapper.writeValue(out,"Invalid Credentials!");
            }
            else {
                mapper.writeValue(out,"Unauthorized User!");
            }
            out.flush();
        });
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .authenticationEntryPoint(authenticationEntryPoint()))
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(e->e.requestMatchers(HttpMethod.GET,"/books","/books/**").permitAll())
                .authorizeHttpRequests(e -> e.requestMatchers("/books/**").authenticated())
                .authorizeHttpRequests(e->e.requestMatchers("/users/**").permitAll())
                .authorizeHttpRequests(e->e.requestMatchers(HttpMethod.GET,"/orders/**").permitAll())
                .authorizeHttpRequests(e -> e.requestMatchers("/orders/**").authenticated())
                .authorizeHttpRequests(e->e.anyRequest().permitAll()).exceptionHandling(c ->
                        c.accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(jwtTokenValidatorFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}
