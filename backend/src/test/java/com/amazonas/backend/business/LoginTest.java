package com.amazonas.backend.business;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {

    @Test
    public void testValidLogin() {
        boolean result = login("admin", "admin");
        assertTrue(result);
    }

    @Test
    public void testInvalidUsername() {
        boolean result = login("invalidUser", "validPassword");
        assertFalse(result);
    }

    @Test
    public void testInvalidPassword() {
        boolean result = login("validUser", "invalidPassword");
        assertFalse(result);
    }

    // Dummy login method for illustration purposes
    private boolean login(String username, String password) {
        // Implement actual login logic
        return "validUser".equals(username) && "validPassword".equals(password);
    }
}
