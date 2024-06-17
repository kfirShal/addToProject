package com.amazonas.backend.business.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public final class UserCredentials implements UserDetails {
    private final String userId;
    private final String password;

    public UserCredentials(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    public String userId() {
        return userId;
    }

    public String password() {
        return password;
    }
}
