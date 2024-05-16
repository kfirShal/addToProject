package com.amazonas.business.authentication;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationController {

    private Map<String,String> userIdToToken;

    public AuthenticationController() {
        userIdToToken = new HashMap<>();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String userId = request.username();
        String password = request.password();

        return null;
    }

    public boolean isTokenValid(String userId, String token) {
        String storedToken = userIdToToken.get(userId);
        return storedToken != null && storedToken.equals(token);
    }
}
