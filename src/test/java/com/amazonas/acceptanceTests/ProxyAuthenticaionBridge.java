package com.amazonas.acceptanceTests;

public class ProxyAuthenticaionBridge implements AuthenticationBridge{
    @Override
    public boolean authenticate(String userId, String password) {
        return false;
    }
}
