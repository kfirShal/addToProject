package com.amazonas.backend.api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTAuthentication extends UsernamePasswordAuthenticationToken {
    public JWTAuthentication() {
        super(null,null,null);
    }
}
