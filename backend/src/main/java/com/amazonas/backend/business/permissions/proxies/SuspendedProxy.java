package com.amazonas.backend.business.permissions.proxies;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.suspended.SuspendedController;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.common.dtos.Suspend;
import com.amazonas.common.permissions.actions.UserActions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component("suspendedProxy")
public class SuspendedProxy extends ControllerProxy{
    private final SuspendedController real;

    protected SuspendedProxy(PermissionsController perm, AuthenticationController auth, SuspendedController suspendedController) {
        super(perm, auth);
        this.real = suspendedController;
    }

    public List<Suspend> getSuspendList(String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.LIST_SUSPENDS);
        return real.getSuspendList();
    }

    public void addSuspend(Suspend suspend, String userId, String token) throws NoPermissionException, AuthenticationFailedException{
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.ADD_SUSPEND);
        real.addSuspend(suspend);
    }

    public Suspend removeSuspend(String id, String userId, String token)  throws NoPermissionException, AuthenticationFailedException{
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.REMOVE_SUSPEND);
        return real.removeSuspend(id);

    }

    public boolean isSuspended(String id, String userId, String token)  throws NoPermissionException, AuthenticationFailedException{
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.IS_SUSPEND);
        return real.isSuspended(id);
    }

}
