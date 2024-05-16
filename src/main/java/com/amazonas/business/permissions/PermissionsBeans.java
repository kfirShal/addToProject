package com.amazonas.business.permissions;

import com.amazonas.business.permissions.profiles.DefaultPermissionsProfile;
import com.amazonas.business.permissions.profiles.PermissionsProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermissionsBeans {

    @Bean
    public PermissionsProfile guestPermissionsProfile() {
        return new DefaultPermissionsProfile("guest");
    }

    @Bean
    public PermissionsProfile defaultRegisteredUserPermissionsProfile() {
        return new DefaultPermissionsProfile("default_registered_user");
    }
}
