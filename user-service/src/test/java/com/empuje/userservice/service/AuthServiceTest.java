package com.empuje.userservice.service;

import com.empuje.userservice.dto.LoginRequest;
import com.empuje.userservice.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void authenticateUser_WithValidCredentials_ReturnsToken() {
        // Arrange
        when(authenticationManager.authenticate(any()))
            .thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class)))
            .thenReturn("dummy-jwt-token");

        // Act
        String token = authService.authenticateUser(loginRequest);

        // Assert
        assertNotNull(token);
        assertEquals("dummy-jwt-token", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(any(Authentication.class));
    }

    @Test
    void authenticateUser_WithInvalidCredentials_ThrowsException() {
        // Arrange
        when(authenticationManager.authenticate(any()))
            .thenThrow(new RuntimeException("Bad credentials"));

        // Act & Assert
        assertThrows("Credenciales inválidas", 
            RuntimeException.class,
            () -> authService.authenticateUser(loginRequest)
        );
    }
}
