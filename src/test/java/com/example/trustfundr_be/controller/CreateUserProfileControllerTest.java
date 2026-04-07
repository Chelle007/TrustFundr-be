package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.trustfundr_be.exception.UserProfileException;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
class CreateUserProfileControllerTest {

    @Mock
    private UserProfileRepository userProfileRepository;

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
        when(userProfileRepository.save(org.mockito.ArgumentMatchers.any(UserProfile.class))).thenReturn(saved);

        // Create controller
        CreateUserProfileController controller = new CreateUserProfileController(userProfileRepository);

        // Create request
        CreateUserProfileController.CreateUserProfileRequest req = new CreateUserProfileController.CreateUserProfileRequest();
        req.setName("  Admin  ");
        req.setDescription("desc");

        // Create response
        CreateUserProfileController.CreateUserProfileResponse res = controller.createUserProfile(req);

        // Assert response
        assertNotNull(res, "Response should not be null");
        assertEquals(id, res.id(), "Response ID should match saved ID");
        assertEquals("Admin", res.name(), "Response name should match saved name");
        assertEquals("desc", res.description(), "Response description should match saved description");
    }

    @Test
    void createUserProfile_blankName() {
        // Create controller
        CreateUserProfileController controller = new CreateUserProfileController(userProfileRepository);

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
        CreateUserProfileController controller = new CreateUserProfileController(userProfileRepository);

        // Create request
        CreateUserProfileController.CreateUserProfileRequest req = new CreateUserProfileController.CreateUserProfileRequest();
        req.setName("Admin");

        // Assert exception
        UserProfileException ex = assertThrows(UserProfileException.class, () -> controller.createUserProfile(req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus(), "Exception status should be CONFLICT");
    }
}

