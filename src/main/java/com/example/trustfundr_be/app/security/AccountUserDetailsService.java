package com.example.trustfundr_be.app.security;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.trustfundr_be.model.UserAccountModel;
import com.example.trustfundr_be.repository.UserAccount;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final UserAccount userAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccountModel account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (account.isDeleted()) {
            throw new DisabledException(
                    "User account has been deactivated. Please contact an administrator");
        }
        if (account.getUserProfile() == null || account.getUserProfile().isDeleted()) {
            throw new DisabledException(
                    "User profile has been suspended. Please contact an administrator");
        }
        String role = "USER";
        String profileName = account.getUserProfile().getName();
        if ("Admin".equalsIgnoreCase(profileName)) {
            role = "ADMIN";
        } else if ("Fund Raiser".equalsIgnoreCase(profileName)) {
            role = "FUNDRAISER";
        } else if ("Donee".equalsIgnoreCase(profileName)) {
            role = "DONEE";
        } else if ("Platform Manager".equalsIgnoreCase(profileName)
                || "Platform Management".equalsIgnoreCase(profileName)) {
            role = "PLATFORM_MANAGEMENT";
        }
        return User.builder()
                .username(account.getUsername())
                .password(account.getPasswordHashString())
                .roles(role)
                .build();
    }
}
