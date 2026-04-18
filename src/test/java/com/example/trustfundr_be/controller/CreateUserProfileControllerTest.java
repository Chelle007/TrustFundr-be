package com.example.trustfundr_be.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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
        UserProfile saved = new UserProfile();
        UUID id = UUID.randomUUID();
        saved.setId(id);
        saved.setName("Admin");
        saved.setDescription("desc");

        when(userProfileRepository.createUserProfile(any(CreateUserProfileController.CreateUserProfileRequest.class)))
                .thenReturn(saved);

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

        CreateUserProfileController controller = new CreateUserProfileController(userProfileRepository, modelMapper);

        CreateUserProfileController.CreateUserProfileRequest req = new CreateUserProfileController.CreateUserProfileRequest();
        req.setName("  Admin  ");
        req.setDescription("desc");

        CreateUserProfileController.CreateUserProfileResponse res = controller.createUserProfile(req);

        assertNotNull(res, "Response should not be null");
        assertEquals(id, res.getId(), "Response ID should match saved ID");
        assertEquals("Admin", res.getName(), "Response name should match saved name");
        assertEquals("desc", res.getDescription(), "Response description should match saved description");
    }
}
