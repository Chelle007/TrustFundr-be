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

import com.example.trustfundr_be.exception.UserProfileException;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
public class SuspendUserProfileControllerTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserProfile userProfile;

    @Test
    void SuspendUserProfile_success() {
        // Setup data
        UUID profile_id = UUID.randomUUID();

        // Mock repository behaviour for finding the profile
        when(userProfileRepository.findById(profile_id)).thenReturn(Optional.of(userProfile));

        // Mock repository behaviour for saving the Profile (returns the same mocked Profile)
        when(userProfileRepository.save(userProfile)).thenReturn(userProfile);

        // Mock model mapper behavior
        SuspendUserProfileController.SuspendUserProfileResponse mockedResponse =
                new SuspendUserProfileController.SuspendUserProfileResponse();
        when(modelMapper.map(eq(userProfile), eq(SuspendUserProfileController.SuspendUserProfileResponse.class)))
                .thenReturn(mockedResponse);

        // Create controller
        SuspendUserProfileController controller = new SuspendUserProfileController(userProfileRepository, modelMapper);

        // Invoke suspend method
        SuspendUserProfileController.SuspendUserProfileResponse result = controller.suspendUserProfile(profile_id);

        // Assert response
        assertNotNull(result, "Response should not be null");

        // Verify interactions(Ensure find, softDelete, and save were ALL called)
        verify(userProfileRepository).findById(profile_id);
        verify(userProfile).softDelete();
        verify(userProfileRepository).save(userProfile);
        verify(modelMapper).map(eq(userProfile), eq(SuspendUserProfileController.SuspendUserProfileResponse.class));
    }

    @Test
    void SuspendUserProfile_failure() throws UserProfileException {
        // Setup data with an ID that does not exist
        UUID Profile_id = UUID.randomUUID();

        // Mock repository to return empty Optional (Simulating use not found)
        when(userProfileRepository.findById(Profile_id)).thenReturn(Optional.empty());

        // Create controller
        SuspendUserProfileController controller = new SuspendUserProfileController(userProfileRepository, modelMapper);

        // Invoke and Assert exception
        UserProfileException exception = assertThrows(UserProfileException.class, () -> {
            controller.suspendUserProfile(Profile_id);
        });

        // Assert exception status and meesage
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus(), "Status should be 404 NOT_FOUND");
        assertEquals("User profile not found", exception.getMessage(), "Error message should match controller definition");

        // Verify repository interactions
        verify(userProfileRepository).findById(Profile_id);
        verify(userProfileRepository, never()).save(any());
        verify(userProfile, never()).softDelete();
    }
}
