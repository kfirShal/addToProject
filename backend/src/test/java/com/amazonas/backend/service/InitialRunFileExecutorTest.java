package com.amazonas.backend.service;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.permissions.proxies.ExternalServicesProxy;
import com.amazonas.backend.business.permissions.proxies.MarketProxy;
import com.amazonas.backend.business.permissions.proxies.StoreProxy;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    StoreProxy storeProxy;
    StoresService storesService;
    UserProfilesService userProfilesService;
    PermissionsService permissionsService;
    String transactionID = "t1";
    String userID = "u1";
    String password = "p1";
    String storeName = "s1";
    String serviceID = "s2";
    String description = "d";


    @BeforeEach
    void setUp() {
        authenticationController = mock(AuthenticationController.class);
        authenticationService = new AuthenticationService(authenticationController);
        externalServicesProxy = mock(ExternalServicesProxy.class);
        externalServicesService = new ExternalServicesService(externalServicesProxy);
        marketProxy = mock(MarketProxy.class);
        marketService = new MarketService(marketProxy);
        notificationsService = mock(NotificationsService.class);
        storeProxy = mock(StoreProxy.class);
        storesService = new StoresService(storeProxy);
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
    void checkSendShipmentSuccess() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment(t1, s2, s1);").getFirst()), Response.class))).success());
    }

    @Test
    void checkSendShipmentFail() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment(t1, s2);").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void missingSemicolonFail() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        try {
            assertFalse(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment(t1, s2, s1)").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void missingSemicolonFail2() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        try {
            assertFalse(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment(t1, s2, s1);sendShipment(t1, s2, s1)").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }

    }

    @Test
    void missingParenthesesFail1() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        try {
            assertFalse(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment t1, s2, s1);").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void missingParenthesesFail2() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment ( t1, s2, s1;").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void missingParenthesesFail3() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment t1, s2, s1 ;").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void missingCommasFail1() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment (t1 s2, s1 );").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void missingCommasFail2() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment (t1 s2 s1 );").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void whiteSpacesNotAffectSuccess() throws AuthenticationFailedException, NoPermissionException {
        when(externalServicesProxy.sendShipment(transactionID, serviceID, storeName, null, null)).thenReturn(true);
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("sendShipment\t(\t\nt1, s2\n, \n\n  \ts1)\t\n  ;").getFirst()), Response.class))).success());
    }

    @Test
    void checkRemoveShippingServiceSuccess() throws AuthenticationFailedException, NoPermissionException {
        doNothing().when(externalServicesProxy).removeShippingService("s2", null, null);
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("removeShippingService( s2 );").getFirst()), Response.class))).success());
    }

    @Test
    void checkRemoveShippingServiceFail() throws AuthenticationFailedException, NoPermissionException {
        doNothing().when(externalServicesProxy).removeShippingService("s2", null, null);
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("removeShippingService( s2, s2 );").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void checkStartMarketSuccess() throws AuthenticationFailedException, NoPermissionException {
        doNothing().when(marketProxy).start( null, null);
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("startMarket( );").getFirst()), Response.class))).success());
    }

    @Test
    void checkStartMarketFail() throws AuthenticationFailedException, NoPermissionException {
        doNothing().when(marketProxy).start( null, null);
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("startMarket( 22 );").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void checkAddStoreSuccess() throws AuthenticationFailedException, NoPermissionException, StoreException {
        when(storeProxy.addStore(userID ,storeName, description, null, null)).thenReturn("1");
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("addStore( u1, s1, d );").getFirst()), Response.class))).success());
    }

    @Test
    void checkAddStoreFail1() throws AuthenticationFailedException, NoPermissionException, StoreException {
        when(storeProxy.addStore(userID ,storeName, description, null, null)).thenReturn("1");
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("addStore( u1, s1, d , 13);").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void checkAddStoreFail2() throws AuthenticationFailedException, NoPermissionException, StoreException {
        when(storeProxy.addStore(userID ,storeName, description, null, null)).thenReturn("1");
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("addStore( u1, s1, d );").getFirst()), Response.class))).success());
        try {
            assertFalse(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("addStore( u1, s1, d );").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void checkOpenStoreSuccess() throws AuthenticationFailedException, NoPermissionException, StoreException {
        when(storeProxy.addStore(userID ,storeName, description, null, null)).thenReturn("1");
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("addStore( u1, s1, d );").getFirst()), Response.class))).success());
        when(storeProxy.openStore("1", null, null)).thenReturn(true);
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("openStore(  s1 );").getFirst()), Response.class))).success());
    }

    @Test
    void checkOpenStoreFail1() throws AuthenticationFailedException, NoPermissionException, StoreException {
        when(storeProxy.addStore(userID ,storeName, description, null, null)).thenReturn("1");
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("addStore( u1, s1, d );").getFirst()), Response.class))).success());
        when(storeProxy.openStore("2", null, null)).thenReturn(true);
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("openStore(  s1 );").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void checkOpenStoreFail2() throws AuthenticationFailedException, NoPermissionException, StoreException {
        when(storeProxy.openStore("1", null, null)).thenReturn(true);
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("openStore(  s1 );").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }

    @Test
    void checkOpenStoreFail3() throws AuthenticationFailedException, NoPermissionException, StoreException {
        when(storeProxy.addStore(userID ,storeName, description, null, null)).thenReturn("1");
        assertTrue(((Response)(JsonUtils.deserialize(executor.executeOperation(executor.parser("addStore( u1, s1, d );").getFirst()), Response.class))).success());
        try {
            assertFalse(((Response) (JsonUtils.deserialize(executor.executeOperation(executor.parser("openStore(  s1 );").getFirst()), Response.class))).success());
        }
        catch (Exception e) {
            assertFalse(false);
        }
    }




}