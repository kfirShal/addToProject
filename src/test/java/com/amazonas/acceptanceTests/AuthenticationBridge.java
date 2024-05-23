package com.amazonas.acceptanceTests;

public interface AuthenticationBridge {
    boolean authenticate(String userId, String password);

    void testAuthenticateValidUser();
    void testAuthenticateInvalidUser();
    void testAuthenticateInvalidPassword();
}
