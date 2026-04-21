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
public class SuspendUserAccountControllerTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserAccount userAccount;

    @Mock
    private UserProfile userProfile;

    @Test
    void SuspendUserAccount_success() {
        // Setup data
        UUID account_id = UUID.randomUUID();
        UUID profile_id = UUID.randomUUID();
        String profileName = "Admin Profile";

        // Mock repository behaviour for finding the account
        when(userAccountRepository.findById(account_id)).thenReturn(Optional.of(userAccount));

        // Mock repository behaviour for saving the account (returns the same mocked account)
        when(userAccountRepository.save(userAccount)).thenReturn(userAccount);

        // Mock profile data for the mapping logic
        when(userAccount.getUserProfile()).thenReturn(userProfile);
        when(userProfile.getId()).thenReturn(profile_id);
        when(userProfile.getName()).thenReturn(profileName);

        // Mock model mapper behavior
        SuspendUserAccountController.SuspendUserAccountResponse mockedResponse =
                new SuspendUserAccountController.SuspendUserAccountResponse();
        when(modelMapper.map(eq(userAccount), eq(SuspendUserAccountController.SuspendUserAccountResponse.class)))
                .thenReturn(mockedResponse);

        // Create controller
        SuspendUserAccountController controller = new SuspendUserAccountController(userAccountRepository, modelMapper);

        // Invoke suspend method
        SuspendUserAccountController.SuspendUserAccountResponse result = controller.suspendUserAccount(account_id);

        // Assert response
        assertNotNull(result, "Response should not be null");
        assertEquals(profile_id, result.getUserProfileId(), "User profile ID should be mapped corrrectly");
        assertEquals(profileName, result.getUserProfileName(), "User profile name should be mapped correctly");

        // Verify interactions(Ensure find, softDelete, and save were ALL called)
        verify(userAccountRepository).findById(account_id);
        verify(userAccount).softDelete();
        verify(userAccountRepository).save(userAccount);
        verify(modelMapper).map(eq(userAccount), eq(SuspendUserAccountController.SuspendUserAccountResponse.class));
    }

    @Test
    void SuspendUserAccount_failure() throws UserAccountException {
        // Setup data with an ID that does not exist
        UUID account_id = UUID.randomUUID();

        // Mock repository to return empty Optional (Simulating use not found)
        when(userAccountRepository.findById(account_id)).thenReturn(Optional.empty());

        // Create controller
        SuspendUserAccountController controller = new SuspendUserAccountController(userAccountRepository, modelMapper);

        // Invoke and Assert exception
        UserAccountException exception = assertThrows(UserAccountException.class, () -> {
            controller.suspendUserAccount(account_id);
        });

        // Assert exception status and meesage
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus(), "Status should be 404 NOT_FOUND");
        assertEquals("User account not found", exception.getMessage(), "Error message should match controller definition");

        // Verify repository interactions
        verify(userAccountRepository).findById(account_id);
        verify(userAccountRepository, never()).save(any());
        verify(userAccount, never()).softDelete();
    }
}
