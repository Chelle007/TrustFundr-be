package com.example.trustfundr_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.trustfundr_be.controller.UpdateUserAccountController;
import com.example.trustfundr_be.exception.UserAccountException;
import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.model.UserProfile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID>, UserAccountRepositoryCustom {

    @Query("SELECT DISTINCT a FROM UserAccount a LEFT JOIN FETCH a.userProfile ORDER BY a.username ASC")
    List<UserAccount> findAllWithUserProfileOrderByUsernameAsc();
    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findByUsernameIgnoreCase(String username);

    @Query("SELECT DISTINCT a FROM UserAccount a LEFT JOIN FETCH a.userProfile p WHERE "
            + "LOWER(a.username) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "LOWER(a.fullName) LIKE LOWER(CONCAT('%', :q, '%')) OR "
            + "(p IS NOT NULL AND LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))) "
            + "ORDER BY LOWER(a.fullName) ASC, LOWER(a.username) ASC")
    List<UserAccount> searchByKeyword(@Param("q") String q);
}

interface UserAccountRepositoryCustom {

    Optional<UserAccount> findByUsernameAndPassword(String username, String password);

    UserAccount updateUserAccount(UUID id, UpdateUserAccountController.UpdateUserAccountRequest request);

    UserAccount suspendUserAccount(UUID id);
}

class UserAccountRepositoryImpl implements UserAccountRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;

    UserAccountRepositoryImpl(PasswordEncoder passwordEncoder, UserProfileRepository userProfileRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserAccount updateUserAccount(UUID id, UpdateUserAccountController.UpdateUserAccountRequest request) {
        UserAccount userAccount = entityManager.find(UserAccount.class, id);
        if (userAccount == null) {
            throw new UserAccountException(HttpStatus.NOT_FOUND, "User account not found");
        }
        if (request.getUserProfileId() != null) {
            UserProfile profile = userProfileRepository.findById(request.getUserProfileId())
                    .orElseThrow(() -> new UserAccountException(HttpStatus.NOT_FOUND, "User profile not found"));
            userAccount.setUserProfile(profile);
        }
        userAccount.setFullName(request.getFullName());
        userAccount.setUsername(request.getUsername());
        String password = request.getPassword();
        if (password != null && !password.isBlank()) {
            userAccount.setPasswordHashString(passwordEncoder.encode(password));
        }
        entityManager.flush();
        return userAccount;
    }

    @Override
    public UserAccount suspendUserAccount(UUID id) {
        UserAccount userAccount = entityManager.find(UserAccount.class, id);
        if (userAccount == null) {
            throw new UserAccountException(HttpStatus.NOT_FOUND, "User account not found");
        }
        userAccount.softDelete();
        entityManager.flush();
        return userAccount;
    }

    @Override
    public Optional<UserAccount> findByUsernameAndPassword(String username, String password) {
        List<UserAccount> results = entityManager
                .createQuery(
                        "SELECT DISTINCT a FROM UserAccount a JOIN FETCH a.userProfile p WHERE a.username = :username",
                        UserAccount.class)
                .setParameter("username", username)
                .getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        UserAccount account = results.get(0);
        if (account.getUserProfile() == null || account.getUserProfile().isDeleted()) {
            return Optional.empty();
        }
        if (!passwordEncoder.matches(password, account.getPasswordHashString())) {
            return Optional.empty();
        }
        return Optional.of(account);
    }
}
