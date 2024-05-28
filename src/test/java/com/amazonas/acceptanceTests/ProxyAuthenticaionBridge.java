package com.amazonas.acceptanceTests;

public class ProxyAuthenticaionBridge implements AuthenticationBridge{
    @Override
    public boolean authenticate(String userId, String password) {
        return false;
    }

    @Override
    public void testAuthenticateValidUser() {

    }

    @Override
    public void testAuthenticateInvalidUser() {

    }

    @Override
    public void testAuthenticateInvalidPassword() {

    }
}
