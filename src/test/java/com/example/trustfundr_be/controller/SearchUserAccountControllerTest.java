package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import com.example.trustfundr_be.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import com.example.trustfundr_be.exception.UserAccountException;
import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
public class SearchUserAccountControllerTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserAccount userAccount;

    @Mock
    private UserProfile userProfile;

    @Test
    void searchUserAccounts_success() {
        // Mock repository to return list containing user account
        String q = "test";
        when(userAccountRepository.searchByKeyword(q)).thenReturn(List.of(userAccount));

        // Mock account and profile for mapping verification
        UUID profileId = UUID.randomUUID();
        String profileName  = "Admin Profile";
        when(userAccount.getUserProfile()).thenReturn(userProfile);
        when(userProfile.getId()).thenReturn(profileId);
        when(userProfile.getName()).thenReturn(profileName);

        // Mock model mapper to return response DTO
        SearchUserAccountController.SearchUserAccountResponse mockedResponse = new SearchUserAccountController.SearchUserAccountResponse();
        when(modelMapper.map(eq(userAccount), eq(SearchUserAccountController.SearchUserAccountResponse.class)))
                .thenReturn(mockedResponse);

        // Create controller
        SearchUserAccountController controller = new SearchUserAccountController(userAccountRepository, modelMapper);

        // Invoke search
        List<SearchUserAccountController.SearchUserAccountResponse> res = controller.searchUserAccounts(q);

        // Assert response
        assertNotNull(res, "Response should not be null");
        assertEquals(1, res.size(), "Response size should be 1");
        assertEquals(profileId, res.get(0).getUserProfileId(), "Profile ID should be mapped correctly");
        assertEquals(profileName, res.get(0).getUserProfileName(), "Profile Name should be mapped correctly");

        verify(userAccountRepository).searchByKeyword(q);
    }

    @Test
    void searchUserAccounts_empty() throws Exception {
        // Set up data with a blank query string
        String q = " ";

        // Create controller
        SearchUserAccountController controller = new SearchUserAccountController(userAccountRepository, modelMapper);

        // Invoke search and Assert exception
        UserAccountException exception = assertThrows(UserAccountException.class, () -> {
            controller.searchUserAccounts(q);
        });

        //Assert response status and message
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus(), "Status should match 400 BAD_REQUEST");
        assertEquals("Search query is required", exception.getMessage(), "Error message should match controller definition");

        verify(userAccountRepository, never()).searchByKeyword(any());
    }
}
