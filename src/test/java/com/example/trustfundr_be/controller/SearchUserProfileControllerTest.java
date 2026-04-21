package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;

import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
class SearchUserProfileControllerTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserProfile userProfile;

    @Test
    void searchUserProfiles_success() {
        // Setup data
        String q = " admin ";
        String term = "admin";

        // Mock repository to return a list containing the profile with specific sorting
        when(userProfileRepository.searchByKeyword(eq(term), any(Sort.class)))
                .thenReturn(List.of(userProfile));

        // Mock model mapper to convert entity to response DTO
        SearchUserProfileController.SearchUserProfileResponse mockedResonse =
                new SearchUserProfileController.SearchUserProfileResponse();
        when(modelMapper.map(eq(userProfile), eq(SearchUserProfileController.SearchUserProfileResponse.class)))
                .thenReturn(mockedResonse);

        // Create controller
        SearchUserProfileController controller = new SearchUserProfileController(userProfileRepository, modelMapper);

        // Invoke search
        List<SearchUserProfileController.SearchUserProfileResponse> result = controller.searchUserProfiles(q);

        // Assert response
        assertNotNull(result, "Result list should not be null");
        assertEquals(1, result.size(), "Result list size should match");

        // Verify interactions
        verify(userProfileRepository).searchByKeyword(eq(term), any(Sort.class));
        verify(modelMapper).map(eq(userProfile), eq(SearchUserProfileController.SearchUserProfileResponse.class));

    }
}
