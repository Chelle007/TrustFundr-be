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

import com.example.trustfundr_be.model.UserAccountModel;
import com.example.trustfundr_be.model.UserProfileModel;
import com.example.trustfundr_be.repository.UserAccount;

@ExtendWith(MockitoExtension.class)
class SearchUserAccountControllerTest {

    @Mock
    private UserAccount userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserAccountModel userAccount;

    @Mock
    private UserProfileModel userProfile;

    @Test
    void searchUserAccounts_success() {
        // Mock repository to return list containing user account
        String q = " test ";
        String term = "test";
        when(userAccountRepository.searchByKeyword(term)).thenReturn(List.of(userAccount));

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

        verify(userAccountRepository).searchByKeyword(term);
    }
}
