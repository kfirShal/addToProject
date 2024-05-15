package com.amazonas.business.permissions;

import com.amazonas.business.permissions.profiles.DefaultPermissionsProfile;
import com.amazonas.business.permissions.profiles.PermissionsProfile;
import com.amazonas.business.permissions.profiles.RegisteredUserPermissionsProfile;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

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
