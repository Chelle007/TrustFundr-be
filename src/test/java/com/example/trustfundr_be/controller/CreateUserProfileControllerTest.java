package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
class CreateUserProfileControllerTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void createUserProfile_success() {
        // Mock user profile repository to return empty optional
        when(userProfileRepository.findByNameIgnoreCase("Admin")).thenReturn(Optional.empty());

        // Create user profile
        UserProfile saved = new UserProfile();
        UUID id = UUID.randomUUID();
        saved.setId(id);
        saved.setName("Admin");
        saved.setDescription("desc");

        // Mock user profile repository to return saved user profile
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(saved);

        when(modelMapper.map(any(UserProfile.class), eq(CreateUserProfileController.CreateUserProfileResponse.class)))
                .thenAnswer(invocation -> {
                    UserProfile up = invocation.getArgument(0);
                    CreateUserProfileController.CreateUserProfileResponse r =
                            new CreateUserProfileController.CreateUserProfileResponse();
                    r.setId(up.getId());
                    r.setName(up.getName());
                    r.setDescription(up.getDescription());
                    return r;
                });

        // Create controller
        CreateUserProfileController controller = new CreateUserProfileController(userProfileRepository, modelMapper);

        // Create request
        CreateUserProfileController.CreateUserProfileRequest req = new CreateUserProfileController.CreateUserProfileRequest();
        req.setName("  Admin  ");
        req.setDescription("desc");

        // Create response
        CreateUserProfileController.CreateUserProfileResponse res = controller.createUserProfile(req);

        // Assert response
        assertNotNull(res, "Response should not be null");
        assertEquals(id, res.getId(), "Response ID should match saved ID");
        assertEquals("Admin", res.getName(), "Response name should match saved name");
        assertEquals("desc", res.getDescription(), "Response description should match saved description");
    }

    @Test
    void createUserProfile_blankName() {
        // Create controller
        CreateUserProfileController controller = new CreateUserProfileController(userProfileRepository, modelMapper);

        // Create request
        CreateUserProfileController.CreateUserProfileRequest req = new CreateUserProfileController.CreateUserProfileRequest();
        req.setName("   ");

        // Assert exception
        UserProfileException ex = assertThrows(UserProfileException.class, () -> controller.createUserProfile(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus(), "Exception status should be BAD_REQUEST");
    }

    @Test
    void createUserProfile_duplicateName() {
        // Mock user profile repository to return existing user profile
        when(userProfileRepository.findByNameIgnoreCase("Admin")).thenReturn(Optional.of(new UserProfile()));

        // Create controller
        CreateUserProfileController controller = new CreateUserProfileController(userProfileRepository, modelMapper);

        // Create request
        CreateUserProfileController.CreateUserProfileRequest req = new CreateUserProfileController.CreateUserProfileRequest();
        req.setName("Admin");

        // Assert exception
        UserProfileException ex = assertThrows(UserProfileException.class, () -> controller.createUserProfile(req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus(), "Exception status should be CONFLICT");
    }
}

