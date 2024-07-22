package com.amazonas.backend.service;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.permissions.proxies.ExternalServicesProxy;
import com.amazonas.backend.business.permissions.proxies.MarketProxy;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InitialRunFileExecutorTest {
    InitialRunFileExecutor executor;
    AuthenticationService authenticationService;
    AuthenticationController authenticationController;
    ExternalServicesProxy externalServicesProxy;
    ExternalServicesService externalServicesService;
    MarketProxy marketProxy;
    MarketService marketService;
    NotificationsService notificationsService;
    StoresService storesService;
    UserProfilesService userProfilesService;
    PermissionsService permissionsService;
    String transactionID = "t1";
    String userID = "u1";
    String password = "p1";
    String storeName = "s1";
    String serviceID = "s2";



    @BeforeEach
    void setUp() {
        authenticationController = mock(AuthenticationController.class);
        authenticationService = new AuthenticationService(authenticationController);
        externalServicesProxy = mock(ExternalServicesProxy.class);
        externalServicesService = new ExternalServicesService(externalServicesProxy);
        marketProxy = mock(MarketProxy.class);
        marketService = new MarketService(marketProxy);
        notificationsService = mock(NotificationsService.class);
        storesService = mock(StoresService.class);
        userProfilesService = mock(UserProfilesService.class);
        permissionsService = mock(PermissionsService.class);
        executor = new InitialRunFileExecutor(authenticationService,
                                              externalServicesService,
                                              marketService,
                                              notificationsService,
                                              storesService,
                                              userProfilesService,
                                              permissionsService);
    }

    @Test
    void checkSendShipment() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment(t1, s2, s1);").getFirst()), Response.class))).success());
    }

    @Test
    void checkRemoveShippingService() throws AuthenticationFailedException, NoPermissionException {
        doNothing().when(externalServicesProxy).removePaymentService(serviceID, null, null);
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("removeShippingService( s2);").getFirst()), Response.class))).success());
    }

    @Test
    void checkStartMarket() throws AuthenticationFailedException, NoPermissionException {
        doNothing().when(marketProxy).start( null, null);
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("startMarket( );").getFirst()), Response.class))).success());
    }


}