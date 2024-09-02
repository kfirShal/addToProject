package com.amazonas.backend.business.permissions.proxies;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.suspended.SuspendedController;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.common.permissions.actions.UserActions;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("suspendedProxy")
public class SuspendedProxy extends ControllerProxy{
    private final SuspendedController real;

    protected SuspendedProxy(PermissionsController perm, AuthenticationController auth, SuspendedController suspendedController) {
        super(perm, auth);
        this.real = suspendedController;
    }

    public List<String> getSuspendList(String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.LIST_SUSPENDS);
        return real.getSuspendList();
    }

    public void addSuspend(String id, String userId, String token) throws NoPermissionException, AuthenticationFailedException{
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.ADD_SUSPEND);
        real.addSuspend(id);
    }

    public boolean removeSuspend(String id, String userId, String token)  throws NoPermissionException, AuthenticationFailedException{
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.REMOVE_SUSPEND);
        return real.removeSuspend(id);

    }

    public boolean isIDInList(String id, String userId, String token)  throws NoPermissionException, AuthenticationFailedException{
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.IS_ID_SUSPEND);
        return real.isIDInList(id);
    }
}
