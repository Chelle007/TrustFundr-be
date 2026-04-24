package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
public class UpdateUserAccountControllerTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserAccount userAccount;

    @Mock
    private UserProfile userProfile;

    @Test
    void updateUserAccount_success() {
        // Setup data
        UUID accountId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        String profileName = "Admin Profile";

        // Create a mock request body
        UpdateUserAccountController.UpdateUserAccountRequest request = new UpdateUserAccountController.UpdateUserAccountRequest();
        request.setFullName("Jane Chen");
        request.setUsername("janechen1005");

        // Mock repository behaviour for updating the account
        // Notice we mock the custom updateUserAccount method here
        when(userAccountRepository.updateUserAccount(eq(accountId), eq(request))).thenReturn(userAccount);

        // Mock profile data for the mapping logic
        when(userAccount.getUserProfile()).thenReturn(userProfile);
        when(userProfile.getId()).thenReturn(profileId);
        when(userProfile.getName()).thenReturn(profileName);

        // Mock model mapper behavior
        UpdateUserAccountController.UpdateUserAccountResponse mockedResponse =
                new UpdateUserAccountController.UpdateUserAccountResponse();
        when(modelMapper.map(eq(userAccount), eq(UpdateUserAccountController.UpdateUserAccountResponse.class)))
                .thenReturn(mockedResponse);

        // Create controller
        UpdateUserAccountController controller = new UpdateUserAccountController(userAccountRepository, modelMapper);

        // Invoke update method
        UpdateUserAccountController.UpdateUserAccountResponse result = controller.updateUserAccount(accountId, request);

        // Assert response
        assertNotNull(result, "Response should not be null");
        assertEquals(profileId, result.getUserProfileId(), "User profile name should be mapped correctly");
        assertEquals(profileName, result.getUserProfileName(), "User profile name should be mappeed correctly");

        // Verify interactions
        verify(userAccountRepository).updateUserAccount(accountId, request);
        verify(modelMapper).map(eq(userAccount), eq(UpdateUserAccountController.UpdateUserAccountResponse.class));

    }

    @Test
    void updateUserAccount_failure() throws Exception{
        // Setup data
        UUID accountId = UUID.randomUUID();
        UpdateUserAccountController.UpdateUserAccountRequest request = new UpdateUserAccountController.UpdateUserAccountRequest();

        // Mock repository to THROW an exception (Simulating user not found in the DB layer)
        when(userAccountRepository.updateUserAccount(eq(accountId), eq(request)))
                .thenThrow(new UserAccountException(HttpStatus.NOT_FOUND, "User account not found"));

        // Create controller
        UpdateUserAccountController controller = new UpdateUserAccountController(userAccountRepository, modelMapper);

        // Invoke and Assert exception
        UserAccountException exception = assertThrows(UserAccountException.class, () -> {
            controller.updateUserAccount(accountId, request);
        });

        // Assert exception status and message
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus(), "Status should be 404 NOT_FOUND");
        assertEquals("User account not found", exception.getMessage(), "Error message should match repository exception");

        // Verify repository interactions
        verify(userAccountRepository).updateUserAccount(accountId, request);
        verify(modelMapper, never()).map(any(), any());
    }
}
