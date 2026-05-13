package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import com.example.trustfundr_be.model.UserProfileModel;
import com.example.trustfundr_be.repository.UserProfile;

@ExtendWith(MockitoExtension.class)
public class UpdateUserProfileControllerTest {

    @Mock
    private UserProfile userProfileRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserProfileModel userProfile;

    @Test
    void updateUserProfile_success() {
        // Setup data
        UUID profileId = UUID.randomUUID();
        UpdateUserProfileController.UpdateUserProfileRequest request = new UpdateUserProfileController.UpdateUserProfileRequest();
        request.setName("New Admin Name");
        request.setDescription("Updated Description");

        // Mock repository behaviour for updating the profile
        when(userProfileRepository.updateUserProfile(eq(profileId), eq(request))).thenReturn(userProfile);

        // Mock model mapper behavior
        UpdateUserProfileController.UpdateUserProfileResponse mockedResponse =
                new UpdateUserProfileController.UpdateUserProfileResponse();
        when(modelMapper.map(eq(userProfile), eq(UpdateUserProfileController.UpdateUserProfileResponse.class)))
                .thenReturn(mockedResponse);

        // Create controller
        UpdateUserProfileController controller = new UpdateUserProfileController(userProfileRepository, modelMapper);

        // Invoke update method
        UpdateUserProfileController.UpdateUserProfileResponse result = controller.updateUserProfile(profileId, request);

        // Assert response
        assertNotNull(result, "Response should not be null");

        // Verify interactions
        verify(userProfileRepository).updateUserProfile(profileId, request);
        verify(modelMapper).map(eq(userProfile), eq(UpdateUserProfileController.UpdateUserProfileResponse.class));

    }

    @Test
    void updateUserProfile_failure()  throws Exception{
        // Setup data
        UUID profileId = UUID.randomUUID();
        UpdateUserProfileController.UpdateUserProfileRequest request = new UpdateUserProfileController.UpdateUserProfileRequest();

        // Mock repository to throw exception (Simulating profile not found in DB layer)
        when(userProfileRepository.updateUserProfile(eq(profileId), eq(request)))
                .thenThrow(new UserProfileException(HttpStatus.NOT_FOUND, "User profile not found"));

        // Create controller
        UpdateUserProfileController controller = new UpdateUserProfileController(userProfileRepository, modelMapper);

        // Invoke and Assert exception
        UserProfileException exception = assertThrows(UserProfileException.class, () -> {
            controller.updateUserProfile(profileId, request);
        });

        // Assert exception status and message (Be careful with case-sensitivity!)
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus(), "Status should be 404 NOT_FOUND");
        assertEquals("User profile not found", exception.getMessage(), "Error message should match repository definition");

        // Verify repository interactions
        verify(userProfileRepository).updateUserProfile(profileId, request);
        verify(modelMapper, never()).map(any(), any());
    }

}
