package com.example.trustfundr_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.trustfundr_be.model.UserAccount;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
}
