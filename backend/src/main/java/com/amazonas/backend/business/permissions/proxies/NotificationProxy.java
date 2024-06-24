package com.amazonas.backend.business.permissions.proxies;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.permissions.PermissionsController;
import org.springframework.stereotype.Component;

@Component("notificationProxy")
public class NotificationProxy extends ControllerProxy{

    protected NotificationProxy(PermissionsController perm, AuthenticationController auth) {
        super(perm, auth);
    }


}
