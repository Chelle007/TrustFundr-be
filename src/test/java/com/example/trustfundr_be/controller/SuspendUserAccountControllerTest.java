package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import com.example.trustfundr_be.exception.UserAccountException;
import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserAccountRepository;

@ExtendWith(MockitoExtension.class)
class SuspendUserAccountControllerTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserAccount userAccount;

    @Mock
    private UserProfile userProfile;

    @Test
    void suspendUserAccount_success() {
        // Setup data
        UUID accountId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        String profileName = "Admin Profile";

        // Mock repository behaviour (controller delegates suspend to repository)
        when(userAccountRepository.suspendUserAccount(accountId)).thenReturn(userAccount);

        // Mock profile data for the mapping logic
        when(userAccount.getUserProfile()).thenReturn(userProfile);
        when(userProfile.getId()).thenReturn(profileId);
        when(userProfile.getName()).thenReturn(profileName);

        // Mock model mapper behavior
        SuspendUserAccountController.SuspendUserAccountResponse mockedResponse =
                new SuspendUserAccountController.SuspendUserAccountResponse();
        when(modelMapper.map(eq(userAccount), eq(SuspendUserAccountController.SuspendUserAccountResponse.class)))
                .thenReturn(mockedResponse);

        // Create controller
        SuspendUserAccountController controller = new SuspendUserAccountController(userAccountRepository, modelMapper);

        // Invoke suspend method
        SuspendUserAccountController.SuspendUserAccountResponse result = controller.suspendUserAccount(accountId);

        // Assert response
        assertNotNull(result, "Response should not be null");
        assertEquals(profileId, result.getUserProfileId(), "User profile ID should be mapped correctly");
        assertEquals(profileName, result.getUserProfileName(), "User profile name should be mapped correctly");

        // Verify interactions
        verify(userAccountRepository).suspendUserAccount(accountId);
        verify(modelMapper).map(eq(userAccount), eq(SuspendUserAccountController.SuspendUserAccountResponse.class));
    }

    @Test
    void suspendUserAccount_notFound() {
        // Setup data with an ID that does not exist
        UUID accountId = UUID.randomUUID();

        // Repository layer throws exception when ID not found
        when(userAccountRepository.suspendUserAccount(accountId))
                .thenThrow(new UserAccountException(HttpStatus.NOT_FOUND, "User account not found"));

        // Create controller
        SuspendUserAccountController controller = new SuspendUserAccountController(userAccountRepository, modelMapper);

        // Invoke and Assert exception
        UserAccountException exception = assertThrows(UserAccountException.class, () -> {
            controller.suspendUserAccount(accountId);
        });

        // Assert exception status and meesage
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus(), "Status should be 404 NOT_FOUND");
        assertEquals("User account not found", exception.getMessage(), "Error message should match controller definition");

        // Verify repository interactions
        verify(userAccountRepository).suspendUserAccount(accountId);
    }
}
