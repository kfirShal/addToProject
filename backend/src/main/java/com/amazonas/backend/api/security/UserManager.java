package com.amazonas.backend.api.security;

import com.amazonas.backend.business.authentication.UserCredentials;
import com.amazonas.backend.repository.UserCredentialsRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service("UserManager")
public class UserManager implements UserDetailsManager, AuthenticationManager {

    private final PasswordEncoder passwordEncoder;
    private final UserCredentialsRepository credentialsRepository;

    public UserManager(UserCredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void updateUser(UserDetails user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        UserCredentials updatedUser = new UserCredentials(user.getUsername(), hashedPassword);
        credentialsRepository.save(updatedUser);
    }

    private boolean passwordsMatch(String toTest, String known) {
        return passwordEncoder.matches(toTest, known);
    }



    //TODO: REMOVE THIS
    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        if(!userExists("admin")) {
            createUser(new UserCredentials("admin", "123"));
        }
    }
}
