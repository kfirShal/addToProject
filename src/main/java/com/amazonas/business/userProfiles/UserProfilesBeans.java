package com.amazonas.business.userProfiles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class UserProfilesBeans {
    @Bean
    public UsersController userController(Map<String, Guest> guests, Map<String,RegisteredUser > registeredUsers) {
        return new UsersControllerImpl(guests,registeredUsers);
    }

    @Bean
    public User guest() {
        return new Guest("guest");
    }

    @Bean
    public User registeredUser() {
        return new RegisteredUser("notGuestAnymore","registeredUser", "password123", "registered@example.com");
    }

    @Bean
    public User admin() {
        return new RegisteredUser("notGuestAnymore","admin", "adminPassword", "admin@example.com");
    }
}
