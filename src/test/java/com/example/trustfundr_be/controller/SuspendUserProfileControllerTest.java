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

import com.example.trustfundr_be.exception.UserProfileException;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
class SuspendUserProfileControllerTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserProfile userProfile;

    @Test
    void suspendUserProfile_success() {
        // Setup data
        UUID profileId = UUID.randomUUID();

        // Mock repository behaviour (controller delegates suspend to repository)
        when(userProfileRepository.suspendUserProfile(profileId)).thenReturn(userProfile);

        // Mock model mapper behavior
        SuspendUserProfileController.SuspendUserProfileResponse mockedResponse =
                new SuspendUserProfileController.SuspendUserProfileResponse();
        when(modelMapper.map(eq(userProfile), eq(SuspendUserProfileController.SuspendUserProfileResponse.class)))
                .thenReturn(mockedResponse);

        // Create controller
        SuspendUserProfileController controller = new SuspendUserProfileController(userProfileRepository, modelMapper);

        // Invoke suspend method
        SuspendUserProfileController.SuspendUserProfileResponse result = controller.suspendUserProfile(profileId);

        // Assert response
        assertNotNull(result, "Response should not be null");

        // Verify interactions
        verify(userProfileRepository).suspendUserProfile(profileId);
        verify(modelMapper).map(eq(userProfile), eq(SuspendUserProfileController.SuspendUserProfileResponse.class));
    }

    @Test
    void suspendUserProfile_notFound() {
        // Setup data with an ID that does not exist
        UUID profileId = UUID.randomUUID();

        // Repository layer throws exception when ID not found
        when(userProfileRepository.suspendUserProfile(profileId))
                .thenThrow(new UserProfileException(HttpStatus.NOT_FOUND, "User profile not found"));

        // Create controller
        SuspendUserProfileController controller = new SuspendUserProfileController(userProfileRepository, modelMapper);

        // Invoke and Assert exception
        UserProfileException exception = assertThrows(UserProfileException.class, () -> {
            controller.suspendUserProfile(profileId);
        });

        // Assert exception status and message
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus(), "Status should be 404 NOT_FOUND");
        assertEquals("User profile not found", exception.getMessage(), "Error message should match controller definition");

        // Verify repository interactions
        verify(userProfileRepository).suspendUserProfile(profileId);
    }
}
