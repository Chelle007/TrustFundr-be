package com.example.trustfundr_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.trustfundr_be.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

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
