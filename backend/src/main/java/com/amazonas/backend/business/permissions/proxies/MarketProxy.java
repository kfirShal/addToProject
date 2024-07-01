package com.amazonas.backend.business.permissions.proxies;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.market.MarketInitializer;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

@Component("marketProxy")
public class MarketProxy extends ControllerProxy {

    private final MarketInitializer real;

    public MarketProxy(MarketInitializer marketInitializer, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = marketInitializer;
    }
    public void start(String userId, String token) throws AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.START_MARKET);
        real.start();
    }

    public void shutdown(String userId, String token) throws AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.SHUTDOWN_MARKET);
    }
}
