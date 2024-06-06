package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;

import java.util.function.Function;

public abstract class ControllerProxy {

    private final AuthenticationController auth;
    protected final PermissionsController perm;

    protected ControllerProxy(PermissionsController perm, AuthenticationController auth) {
        this.perm = perm;
        this.auth = auth;
    }

    protected void authenticateToken(String userId, String token) throws AuthenticationFailedException {
        if (! auth.validateToken(userId,token)) {
            throw new AuthenticationFailedException("Failed to validate authenticity of the user");
        }
    }

    protected void checkPermission(Function<Void,Boolean> test) throws NoPermissionException {
        if (! test.apply(null)) {
            throw new NoPermissionException("User does not have action to perform this action");
        }
    }

}
