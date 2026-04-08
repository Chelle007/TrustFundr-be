package com.example.trustfundr_be.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.trustfundr_be.app.security.JwtService;
import com.example.trustfundr_be.exception.AccountDisabledException;
import com.example.trustfundr_be.exception.AuthException;
import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.repository.UserAccountRepository;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Test
    void login_success() throws Exception {
        // Create user details
        String username = "admin";
        UUID userId = UUID.randomUUID();
        UserDetails principal = User.withUsername(username).password("ignored").roles("ADMIN").build();
        Authentication authResult = UsernamePasswordAuthenticationToken.authenticated(principal, null,
                principal.getAuthorities());

        // Mock authentication manager to return authentication result
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authResult);

        // Create user account
        UserAccount entity = new UserAccount();
        entity.setId(userId);
        entity.setUsername(username);
        entity.setFullName("Test User");

        // Mock user account repository to return user account
        when(userAccountRepository.findByUsername(username)).thenReturn(Optional.of(entity));

        // Create response
        LoginController.LoginResponse mapped = new LoginController.LoginResponse();
        mapped.setId(userId);
        mapped.setUsername(username);
        mapped.setFullName("Test User");
        when(modelMapper.map(eq(entity), eq(LoginController.LoginResponse.class))).thenReturn(mapped);

        // Mock JWT service to return JWT token
        when(jwtService.generateToken(principal)).thenReturn("jwt-token");

        // Create controller
        LoginController controller = new LoginController(authenticationManager, userAccountRepository, modelMapper,
                jwtService);

        // Create request
        LoginController.LoginRequest req = new LoginController.LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        // Create response
        LoginController.LoginResponse res = controller.login(req, httpServletRequest);

        // Assert response
        assertNotNull(res, "Response should not be null");
        assertEquals(userId, res.getId());
        assertEquals("admin", res.getUsername());
        assertEquals("Test User", res.getFullName());
        assertEquals("jwt-token", res.getToken());
    }

    @Test
    void login_bad_credentials() {
        // Mock authentication manager to throw BadCredentialsException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        // Create controller
        LoginController controller = new LoginController(authenticationManager, userAccountRepository, modelMapper,
                jwtService);

        // Create request
        LoginController.LoginRequest req = new LoginController.LoginRequest();
        req.setUsername("admin");
        req.setPassword("wrong");

        // Assert exception
        assertThrows(AuthException.class, () -> controller.login(req, httpServletRequest));
    }

    @Test
    void login_disabled_account() {
        // Mock authentication manager to throw DisabledException
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("disabled"));

        // Create controller
        LoginController controller = new LoginController(authenticationManager, userAccountRepository, modelMapper,
                jwtService);
        LoginController.LoginRequest req = new LoginController.LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        // Assert exception
        assertThrows(AccountDisabledException.class, () -> controller.login(req, httpServletRequest));
    }

    @Test
    void login_account_not_found() {
        
        // Create user details
        String username = "admin";
        UserDetails principal = User.withUsername(username).password("ignored").roles("ADMIN").build();
        Authentication authResult = UsernamePasswordAuthenticationToken.authenticated(principal, null,
                principal.getAuthorities());

        // Mock authentication manager to return authentication result
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authResult);

        // Mock user account repository to return empty optional
        when(userAccountRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Create controller
        LoginController controller = new LoginController(authenticationManager, userAccountRepository, modelMapper,
                jwtService);

        // Create request
        LoginController.LoginRequest req = new LoginController.LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        // Assert exception
        assertThrows(IllegalStateException.class, () -> controller.login(req, httpServletRequest));
    }
}

