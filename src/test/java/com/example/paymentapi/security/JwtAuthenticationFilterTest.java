package com.example.paymentapi.security;

import com.example.paymentapi.entity.User;
import com.example.paymentapi.service.UserService;
import com.example.paymentapi.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    void doFilterInternal_noAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    //    verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractEmail(anyString());
        verify(userService, never()).getUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_invalidAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Invalid");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractEmail(anyString());
        verify(userService, never()).getUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_validJwt() throws ServletException, IOException {
        String token = "testToken";
        String email = "test@example.com";
        User user = new User();
        user.setPassword("password");
        user.setEmail(email);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(userService.getUserByUsername(email)).thenReturn(user);
        when(jwtUtil.validateToken(token)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractEmail(token);

        verify(userService).getUserByUsername(email);

        verify(jwtUtil).validateToken(token);

        SecurityContext securityContext = SecurityContextHolder.getContext();

        assertNotNull(securityContext.getAuthentication());
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, securityContext.getAuthentication());
    }
}
