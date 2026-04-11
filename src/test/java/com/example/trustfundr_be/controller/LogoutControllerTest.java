package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class LogoutControllerTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Test
    void logout_success() throws ServletException {
        // Mock http servlet request to complete logout without error
        doNothing().when(httpServletRequest).logout();

        // Create controller
        LogoutController controller = new LogoutController();

        // Invoke logout
        LogoutController.LogoutResponse res = controller.logout(httpServletRequest);

        // Assert response
        assertNotNull(res, "Response should not be null");
        assertEquals("Logged out successfully", res.message(), "Message should match success string");

        verify(httpServletRequest).logout();
    }
}
