package com.amazonas.business.authentication;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationControllerTest {

    private AuthenticationController authenticationController;
    private String userId;
    private String password;

    @BeforeEach
    void setUp() {
        authenticationController = new AuthenticationController();
        userId = "testUser";
        password = "testPassword";
        authenticationController.addUserCredentials(userId, password);
    }

    @AfterEach
    void tearDown() {
        authenticationController = null;
        userId = null;
        password = null;
    }

    @Test
    void authenticateUserSuccess() {
        AuthenticationResponse response = authenticationController.authenticateUser(userId, password);
        assertTrue(response.success());
        assertNotNull(response.token());
    }

    @Test
    void authenticateUserFailureWrongPassword() {
        AuthenticationResponse response = authenticationController.authenticateUser(userId, "wrongPassword");
        assertFalse(response.success());
        assertNull(response.token());
    }

    @Test
    void revokeAuthenticationSuccess() {
        AuthenticationResponse response = authenticationController.authenticateUser(userId, password);
        assertTrue(response.success());
        assertNotNull(response.token());

        assertTrue(authenticationController.revokeAuthentication(userId));
    }

    @Test
    void revokeAuthenticationFailure() {
        assertFalse(authenticationController.revokeAuthentication(userId));
    }

    @Test
    void validateTokenSuccess() {
        AuthenticationResponse response = authenticationController.authenticateUser(userId, password);
        assertTrue(response.success());
        assertNotNull(response.token());

        String token = response.token();
        assertTrue(authenticationController.validateToken(userId, token));
    }

    @Test
    void validateTokenFailureAfterAuthentication() {
        AuthenticationResponse response = authenticationController.authenticateUser(userId, password);
        assertTrue(response.success());
        assertNotNull(response.token());

        String token = response.token();
        assertFalse(authenticationController.validateToken(userId, token + "invalid"));
    }

    @Test
    void validateTokenFailureBeforeAuthentication() {
        assertFalse(authenticationController.validateToken(userId, "invalidToken"));
    }

    @Test
    void validateTokenFailureAfterRevocation() {
        AuthenticationResponse response = authenticationController.authenticateUser(userId, password);
        assertTrue(response.success());
        assertNotNull(response.token());

        String token = response.token();
        assertTrue(authenticationController.revokeAuthentication(userId));
        assertFalse(authenticationController.validateToken(userId, token));
    }

    @Test
    void resetSecretKeySuccess() {
        AuthenticationResponse response = authenticationController.authenticateUser(userId, password);
        assertTrue(response.success());
        assertNotNull(response.token());

        String token = response.token();
        authenticationController.resetSecretKey();
        assertFalse(authenticationController.validateToken(userId, token));
    }
}