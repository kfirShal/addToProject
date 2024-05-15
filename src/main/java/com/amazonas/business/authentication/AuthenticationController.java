package com.amazonas.business.authentication;

import java.util.Map;

public class AuthenticationController {

    private Map<String,String> userIdToToken;

    public AuthenticationResponse Authenticate (AuthenticationRequest request) {
        return null;
    }

    public boolean isTokenValid(String userId, String token) {
        String storedToken = userIdToToken.get(userId);
        return storedToken != null && storedToken.equals(token);
    }
}
