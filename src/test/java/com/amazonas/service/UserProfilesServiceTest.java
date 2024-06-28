package com.amazonas.service;

import com.amazonas.business.permissions.proxies.StoreProxy;
import com.amazonas.business.permissions.proxies.UserProxy;
import com.amazonas.service.requests.users.CartRequest;
import com.amazonas.service.requests.users.LoginRequest;
import com.amazonas.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class UserProfilesServiceTest {
    public UserProfilesServiceTest() {
        this.usersController = mock(UserProxy.class);
        this.storeProxy = mock(StoreProxy.class);
        this.userProfilesService = new UserProfilesService(this.usersController);
        this.storesService = new StoresService(this.storeProxy);
    }

    private final UserProfilesService userProfilesService;
    private final UserProxy usersController;
    private final StoreProxy storeProxy;
    private final StoresService storesService;
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        try {
            when(usersController.enterAsGuest()).thenReturn("12345");
            doNothing().when(usersController).register("tamirosh@post.bgu.ac.il", "12345", "12345");
            doNothing().when(usersController).register("tamiros@post.bgu.ac.il", "12346", "12346");
            doNothing().when(usersController).loginToRegistered("12345", "12345", "12345");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }



    @Test
    void UsersTryToBuyLastProduct() {
        boolean[] results = new boolean[2];
        AtomicInteger count = new AtomicInteger(0);
        storesService.addProduct(JsonUtils.serialize(new CartRequest("123", "123", 1)));

        Thread thread1 = new Thread() {
            public void run() {
                String userId = userProfilesService.enterAsGuest();
                userProfilesService.loginToRegistered(JsonUtils.serialize(new LoginRequest(userId, "12345")));
                results[0] = JsonUtils.deserialize(storesService.removeProduct(JsonUtils.serialize(new CartRequest("123", "123", 1))), boolean.class);
                count.incrementAndGet();
            }
        };

        Thread thread2 = new Thread() {
            public void run() {
                String userId = userProfilesService.enterAsGuest();
                userProfilesService.loginToRegistered(JsonUtils.serialize(new LoginRequest(userId, "12346")));
                results[1] = JsonUtils.deserialize(userProfilesService.addProductToCart(JsonUtils.serialize(new CartRequest("123", "123", 1))), boolean.class);
                count.incrementAndGet();
            }
        };

        thread1.start();
        thread2.start();

        while (count.get() != 2);
        boolean result = (results[0] && !results[1]) || (!results[0] && results[1]); // xor implementation
        assertTrue(result);
    }



}
