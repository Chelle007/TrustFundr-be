package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;

import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
public class ViewUserProfileControllerTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserProfile userProfile;

    @Test
    void viewUserProfiles_success() {
        // Setup data
        UUID profileId = UUID.randomUUID();
        String name = "AdminProfile";
        String description = "Full access profile";

        // Mock repository to return a list of profiles with Sort parameter
        when(userProfileRepository.findAll(any(Sort.class))).thenReturn(List.of(userProfile));

        // Mock model mapper behavior
        ViewUserProfileController.UserProfileResponse mockedResponse =
                new ViewUserProfileController.UserProfileResponse();
        mockedResponse.setId(profileId);
        mockedResponse.setName(name);
        mockedResponse.setDescription(description);

        when(modelMapper.map(eq(userProfile), eq(ViewUserProfileController.UserProfileResponse.class)))
                .thenReturn(mockedResponse);

        // Create controller
        ViewUserProfileController controller = new ViewUserProfileController(userProfileRepository, modelMapper);

        // Invoke list method
        List<ViewUserProfileController.UserProfileResponse> result = controller.listUserProfiles();

        // Assert response
        assertNotNull(result, "Result list should noty be null");
        assertEquals(1, result.size(), "Result list size should match");
        assertEquals(name, result.get(0).getName(), "Profile name should match");

        // Verify interactions
        verify(userProfileRepository).findAll(any(Sort.class));
        verify(modelMapper).map(eq(userProfile), eq(ViewUserProfileController.UserProfileResponse.class));
    }
}
