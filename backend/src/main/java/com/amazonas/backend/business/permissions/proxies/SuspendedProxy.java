package com.amazonas.backend.business.permissions.proxies;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.suspended.SuspendedController;
import com.amazonas.backend.business.permissions.PermissionsController;

import java.util.List;

public class SuspendedProxy extends ControllerProxy{
    private final SuspendedController real;

    protected SuspendedProxy(PermissionsController perm, AuthenticationController auth, SuspendedController suspendedController) {
        super(perm, auth);
        this.real = suspendedController;
    }

    public List<String> getSuspendList() {
        return real.getSuspendList();
    }

    public void addSuspend(String id){
        real.addSuspend(id);
    }

    public boolean removeSuspend(String id){
        return real.removeSuspend(id);

    }

    public boolean isIDInList(String id){
        return real.isIDInList(id);
    }
}
