package com.amazonas.backend.business.permissions.proxies;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import org.springframework.stereotype.Component;

@Component("permissionsProxy")
public class PermissionsProxy extends ControllerProxy{

    private final PermissionsController real;

    protected PermissionsProxy(PermissionsController perm, AuthenticationController auth, PermissionsController permissionsController) {
        super(perm, auth);
        this.real = permissionsController;
    }

    public PermissionsProfile getUserPermissions(String userId, String token) throws AuthenticationFailedException, IllegalArgumentException {
        authenticateToken(userId, token);
        return real.getPermissionsProfile(userId);
    }

    public PermissionsProfile getGuestPermissions(String userId, String token) throws IllegalArgumentException, AuthenticationFailedException {
        authenticateToken(userId, token);
        return real.getGuestPermissionsProfile();
    }

    public boolean isAdmin(String userId, String token) throws AuthenticationFailedException {
        authenticateToken(userId, token);
        return real.isAdmin(userId);
    }
}
