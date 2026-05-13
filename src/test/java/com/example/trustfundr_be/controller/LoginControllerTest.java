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
import org.springframework.security.core.userdetails.UserDetails;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.trustfundr_be.app.security.JwtService;
import com.example.trustfundr_be.exception.AuthException;
import com.example.trustfundr_be.model.UserAccountModel;
import com.example.trustfundr_be.repository.UserAccount;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private UserAccount userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Test
    void login_success() throws Exception {
        String username = "admin";
        UUID userId = UUID.randomUUID();

        UserAccountModel entity = new UserAccountModel();
        entity.setId(userId);
        entity.setUsername(username);
        entity.setFullName("Test User");

        when(userAccountRepository.login(any(LoginController.LoginRequest.class))).thenReturn(Optional.of(entity));

        LoginController.LoginResponse mapped = new LoginController.LoginResponse();
        mapped.setId(userId);
        mapped.setUsername(username);
        mapped.setFullName("Test User");
        when(modelMapper.map(eq(entity), eq(LoginController.LoginResponse.class))).thenReturn(mapped);

        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        LoginController controller = new LoginController(userAccountRepository, modelMapper, jwtService);

        LoginController.LoginRequest req = new LoginController.LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        LoginController.LoginResponse res = controller.login(req, httpServletRequest);

        assertNotNull(res, "Response should not be null");
        assertEquals(userId, res.getId());
        assertEquals("admin", res.getUsername());
        assertEquals("Test User", res.getFullName());
        assertEquals("jwt-token", res.getToken());
    }

    @Test
    void login_bad_credentials() {
        when(userAccountRepository.login(any(LoginController.LoginRequest.class))).thenReturn(Optional.empty());

        LoginController controller = new LoginController(userAccountRepository, modelMapper, jwtService);

        LoginController.LoginRequest req = new LoginController.LoginRequest();
        req.setUsername("admin");
        req.setPassword("wrong");

        assertThrows(AuthException.class, () -> controller.login(req, httpServletRequest));
    }
}
