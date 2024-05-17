package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.permissions.PermissionsController;

public abstract class ControllerProxy {

    private final AuthenticationController auth;
    protected final PermissionsController perm;

    protected ControllerProxy(PermissionsController perm, AuthenticationController auth) {
        this.perm = perm;
        this.auth = auth;
    }

    protected void validateToken(String userId,String token) {
        if (! auth.validateToken(userId,token)) {
            throw new RuntimeException("Failed to validate authenticity of the user");
        }
    }

}
