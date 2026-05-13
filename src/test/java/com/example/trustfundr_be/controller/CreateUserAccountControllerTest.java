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

import com.example.trustfundr_be.model.UserAccountModel;
import com.example.trustfundr_be.model.UserProfileModel;
import com.example.trustfundr_be.repository.UserAccount;

@ExtendWith(MockitoExtension.class)
class CreateUserAccountControllerTest {

    @Mock
    private UserAccount userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void createUserAccount_success() {
        UUID accountId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        UserProfileModel profile = new UserProfileModel();
        profile.setId(profileId);
        profile.setName("Donee");

        UserAccountModel saved = new UserAccountModel();
        saved.setId(accountId);
        saved.setFullName("New User");
        saved.setUsername("newuser");
        saved.setUserProfile(profile);

        when(userAccountRepository.createUserAccount(any(CreateUserAccountController.CreateUserAccountRequest.class)))
                .thenReturn(saved);

        when(modelMapper.map(any(UserAccountModel.class), eq(CreateUserAccountController.CreateUserAccountResponse.class)))
                .thenAnswer(invocation -> {
                    UserAccountModel acc = invocation.getArgument(0);
                    CreateUserAccountController.CreateUserAccountResponse r =
                            new CreateUserAccountController.CreateUserAccountResponse();
                    r.setId(acc.getId());
                    r.setFullName(acc.getFullName());
                    r.setUsername(acc.getUsername());
                    return r;
                });

        CreateUserAccountController controller =
                new CreateUserAccountController(userAccountRepository, modelMapper);

        CreateUserAccountController.CreateUserAccountRequest req =
                new CreateUserAccountController.CreateUserAccountRequest();
        req.setUserProfileId(profileId);
        req.setFullName("  New User  ");
        req.setUsername("newuser");
        req.setPassword("secret12");

        CreateUserAccountController.CreateUserAccountResponse res = controller.createUserAccount(req);

        assertNotNull(res);
        assertEquals(accountId, res.getId());
        assertEquals("New User", res.getFullName());
        assertEquals("newuser", res.getUsername());
        assertEquals(profileId, res.getUserProfileId());
        assertEquals("Donee", res.getUserProfileName());
    }
}
