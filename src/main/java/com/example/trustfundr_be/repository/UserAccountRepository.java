package com.example.trustfundr_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.trustfundr_be.model.UserAccount;

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
            + "(p IS NOT NULL AND LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))) ORDER BY a.username ASC")
    List<UserAccount> searchByKeyword(@Param("q") String q);
}

interface UserAccountRepositoryCustom {

    Optional<UserAccount> findByUsernameAndPassword(String username, String password);
}

class UserAccountRepositoryImpl implements UserAccountRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;

    UserAccountRepositoryImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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
