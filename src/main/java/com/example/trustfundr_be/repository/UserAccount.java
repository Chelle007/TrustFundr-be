package com.example.trustfundr_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.trustfundr_be.controller.CreateUserAccountController;
import com.example.trustfundr_be.controller.UpdateUserAccountController;
import com.example.trustfundr_be.exception.UserAccountException;
import com.example.trustfundr_be.model.UserAccountModel;
import com.example.trustfundr_be.model.UserProfileModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public interface UserAccount extends JpaRepository<UserAccountModel, UUID>, UserAccountCustom {

    @Query("SELECT DISTINCT a FROM UserAccountModel a LEFT JOIN FETCH a.userProfile ORDER BY a.username ASC")
    List<UserAccountModel> findAllWithUserProfileOrderByUsernameAsc();

    Optional<UserAccountModel> findByUsername(String username);

    Optional<UserAccountModel> findByUsernameIgnoreCase(String username);

    @Query("SELECT DISTINCT a FROM UserAccountModel a LEFT JOIN FETCH a.userProfile p WHERE "
            + "LOWER(a.username) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "LOWER(a.fullName) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(p IS NOT NULL AND LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))) OR "
            + "LOWER(cast(a.id AS string)) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "ORDER BY a.fullName ASC, a.username ASC")
    List<UserAccountModel> searchByKeyword(@Param("q") String q);
}

interface UserAccountCustom {

    Optional<UserAccountModel> findByUsernameAndPassword(String username, String password);

    UserAccountModel updateUserAccount(UUID id, UpdateUserAccountController.UpdateUserAccountRequest request);

    UserAccountModel suspendUserAccount(UUID id);

    UserAccountModel createUserAccount(CreateUserAccountController.CreateUserAccountRequest request);
}

class UserAccountImpl implements UserAccountCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserProfile userProfile;
    private final UserAccount userAccounts;

    UserAccountImpl(
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder,
            UserProfile userProfile,
            @Lazy UserAccount userAccounts) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userProfile = userProfile;
        this.userAccounts = userAccounts;
    }

    @Override
    public UserAccountModel updateUserAccount(UUID id, UpdateUserAccountController.UpdateUserAccountRequest request) {
        UserAccountModel userAccount = entityManager.find(UserAccountModel.class, id);
        if (userAccount == null) {
            throw new UserAccountException(HttpStatus.NOT_FOUND, "User account not found");
        }
        userAccount.setFullName(request.getFullName().trim());
        userAccount.setUsername(request.getUsername().trim());
        if (request.getUserProfileId() != null) {
            UserProfileModel profile = userProfile.findById(request.getUserProfileId())
                    .orElseThrow(() -> new UserAccountException(HttpStatus.NOT_FOUND, "User profile not found"));
            userAccount.setUserProfile(profile);
        }
        String password = request.getPassword();
        if (password != null && !password.isBlank()) {
            userAccount.setPasswordHashString(passwordEncoder.encode(password));
        }
        entityManager.flush();
        return userAccount;
    }

    @Override
    public UserAccountModel suspendUserAccount(UUID id) {
        UserAccountModel userAccount = entityManager.find(UserAccountModel.class, id);
        if (userAccount == null) {
            throw new UserAccountException(HttpStatus.NOT_FOUND, "User account not found");
        }
        userAccount.softDelete();
        entityManager.flush();
        return userAccount;
    }

    @Override
    public UserAccountModel createUserAccount(CreateUserAccountController.CreateUserAccountRequest request) {
        UserAccountModel userAccount = modelMapper.map(request, UserAccountModel.class);
        userAccount.setFullName(request.getFullName().trim());
        userAccount.setUsername(request.getUsername().trim());
        userAccount.setPasswordHashString(passwordEncoder.encode(request.getPassword()));
        userAccount.setUserProfile(userProfile.findById(request.getUserProfileId())
                .orElseThrow(() -> new UserAccountException(HttpStatus.NOT_FOUND, "User profile not found")));
        return userAccounts.save(userAccount);
    }

    @Override
    public Optional<UserAccountModel> findByUsernameAndPassword(String username, String password) {
        List<UserAccountModel> results = entityManager
                .createQuery(
                        "SELECT DISTINCT a FROM UserAccountModel a JOIN FETCH a.userProfile p WHERE a.username = :username",
                        UserAccountModel.class)
                .setParameter("username", username)
                .getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        UserAccountModel account = results.get(0);
        if (account.getUserProfile() == null || account.getUserProfile().isDeleted()) {
            return Optional.empty();
        }
        if (!passwordEncoder.matches(password, account.getPasswordHashString())) {
            return Optional.empty();
        }
        return Optional.of(account);
    }
}
