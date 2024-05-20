package com.amazonas.business.userProfiles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class UserProfilesBeans {
    @Bean
    public UsersController userController(Map<Integer, User> guests, Map<String, User> registeredUsers) {
        return new UsersControllerImpl(guests,registeredUsers);
    }

    @Bean
    public User guest() {
        return new Guest(1);
    }

    @Bean
    public User registeredUser() {
        return new RegisteredUser(2,"registeredUser", "password123", "registered@example.com");
    }

    @Bean
    public User admin() {
        return new RegisteredUser(0,"admin", "adminPassword", "admin@example.com");
    }
}
