package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserAccountRepository;

@ExtendWith(MockitoExtension.class)
class ViewUserAccountControllerTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserAccount userAccount;

    @Mock
    private UserProfile userProfile;

    @Test
    void viewUserAccounts_success() {
        // Setup data
        UUID profileId = UUID.randomUUID();
        String profileName = "Standard User";

        // Mock repository to return a list containing our mocked account
        when(userAccountRepository.findAllWithUserProfileOrderByUsernameAsc())
                .thenReturn(List.of(userAccount));

        // Mock entity relationships for the toResponse private method
        when(userAccount.getUserProfile()).thenReturn(userProfile);
        when(userProfile.getId()).thenReturn(profileId);
        when(userProfile.getName()).thenReturn(profileName);

        // Mock model mapper behavor
        ViewUserAccountController.UserAccountResponse mockedResponse =
                new ViewUserAccountController.UserAccountResponse();
        when(modelMapper.map(eq(userAccount), eq(ViewUserAccountController.UserAccountResponse.class)))
                .thenReturn(mockedResponse);

        // Create controller
        ViewUserAccountController controller = new ViewUserAccountController(userAccountRepository, modelMapper);

        // Invoke list method
        List<ViewUserAccountController.UserAccountResponse> result = controller.listUserAccounts();

        // Assert response list
        assertNotNull(result, "Result list should be null");
        assertEquals(1, result.size(), "Result list should contain 1 account");

        // Assert that the mapping logic correctly extracted profile data
        ViewUserAccountController.UserAccountResponse responseObj = result.get(0);
        assertEquals(profileId, responseObj.getUserProfileId(), "Profile ID should match");
        assertEquals(profileName, responseObj.getUserProfileName(), "Profile Name should match");

        // Verify interactions
        verify(userAccountRepository).findAllWithUserProfileOrderByUsernameAsc();
        verify(modelMapper).map(eq(userAccount), eq(ViewUserAccountController.UserAccountResponse.class));
    }
}
