package com.amazonas.backend.api.security;

import com.amazonas.backend.business.authentication.UserCredentials;
import com.amazonas.backend.repository.UserCredentialsRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("UserManager")
public class UserManager implements UserDetailsManager, AuthenticationManager {

    private final PasswordEncoder passwordEncoder;
    private final UserCredentialsRepository credentialsRepository;
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();


    public UserManager(UserCredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void createUser(UserDetails user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        UserCredentials newUser = new UserCredentials(user.getUsername(), hashedPassword);
        credentialsRepository.insert(newUser);
    }

    @Override
    public void updateUser(UserDetails user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        UserCredentials updatedUser = new UserCredentials(user.getUsername(), hashedPassword);
        credentialsRepository.save(updatedUser);
    }

    @Override
    public void deleteUser(String username) {
        credentialsRepository.deleteById(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
        } else {
            String username = currentUser.getName();                                            //TODO: add this when we have a mongodb
            UserCredentials currentUserFromDB = credentialsRepository.findById(username);  //.orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if(passwordsMatch(oldPassword, currentUserFromDB.getPassword())) {
                String hashedPassword = passwordEncoder.encode(newPassword);
                UserCredentials updatedUser = new UserCredentials(username, hashedPassword);
                credentialsRepository.save(updatedUser);
            } else {
                throw new AccessDeniedException("Can't change password as old password is incorrect.");
            }
        }
    }

    @Override
    public boolean userExists(String username) {
        return credentialsRepository.existsById(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!userExists(username)) {
            throw new UsernameNotFoundException("User not found");
        }
        return credentialsRepository.findById(username);

        //TODO: add this when we have a mongodb
//         return credentialsRepository.findById(username);
//                .map(this::buildUserDetails)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private boolean passwordsMatch(String toTest, String known) {
        return passwordEncoder.matches(toTest, known);
    }

    private UserDetails buildUserDetails(UserCredentials user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = loadUserByUsername(authentication.getName());
        String credentials = (String) authentication.getCredentials();
        if(passwordsMatch(credentials, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } else {
            throw new AccessDeniedException("Bad credentials");
        }
    }

    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        if(!userExists("admin")) {
            createUser(new UserCredentials("admin", "123"));
        }
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }
}
