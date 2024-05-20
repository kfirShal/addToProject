package com.amazonas.business.userProfiles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserProfilesBeans {

    @Bean
    public User systemAdmin() {
        return new RegisteredUser("admin","admin", "adminPassword", "admin@example.com");
    }
}
