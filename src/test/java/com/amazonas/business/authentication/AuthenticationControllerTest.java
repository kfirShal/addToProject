package com.amazonas.business.authentication;

import com.amazonas.repository.UserCredentialsRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {
    private static final MacAlgorithm alg = Jwts.SIG.HS512;

    private static final String passwordStorageFormat = "{bcrypt}";
    private AuthenticationController authenticationController;
    private String userId;
    private String password;
    private String hashedPassword;
    private PasswordEncoder encoder;

    public AuthenticationControllerTest() {
        UserCredentialsRepository ucr = mock(UserCredentialsRepository.class);
        userId = "testUser";
        password = "testPassword";
        encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        hashedPassword = encoder.encode(passwordStorageFormat+password);
        when(ucr.getHashedPassword(userId)).thenReturn(hashedPassword);

        authenticationController = new AuthenticationController(ucr);
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