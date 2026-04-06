package com.example.trustfundr_be.app.security;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.trustfundr_be.model.entity.UserAccount;
import com.example.trustfundr_be.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (account.isDeleted()) {
            throw new DisabledException(
                    "User account has been deactivated. Please contact an administrator");
        }
        String role = "USER";
        if (account.getUserProfile() != null && "Admin".equalsIgnoreCase(account.getUserProfile().getName())) {
            role = "ADMIN";
        }
        return User.builder()
                .username(account.getUsername())
                .password(account.getPasswordHashString())
                .roles(role)
                .build();
    }
}
