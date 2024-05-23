package com.amazonas.acceptanceTests;

public class RealAuthenticationBridge implements AuthenticationBridge{
    @Override
    public boolean authenticate(String userId, String password) {
        return false;
    }
}
