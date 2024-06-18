package com.amazonas.backend.business.authentication;

import com.amazonas.common.utils.ReadWriteLock;
import com.amazonas.backend.repository.UserCredentialsRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AuthenticationController implements UserDetailsManager, AuthenticationManager {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserCredentialsRepository repository;

    private static final MacAlgorithm alg = Jwts.SIG.HS512;
    private final Map<String, String> userIdToUUID;
    private final PasswordEncoder encoder;
    private final ReadWriteLock lock;
    private SecretKey key;

    public AuthenticationController(UserCredentialsRepository userCredentialsRepository) {
        this.repository = userCredentialsRepository;
        key = Jwts.SIG.HS512.key().build();
        userIdToUUID = new HashMap<>();
        lock = new ReadWriteLock();
        encoder = new BCryptPasswordEncoder();
    }

    //TODO: REMOVE THIS
    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        if(!userExists("admin")) {
            createUser(new UserCredentials("admin", "123"));
        }
    }

    public String authenticateGuest(String userid){
        log.debug("Generating token for guest user {}", userid);
        return getToken(userid);
    }

    public String getToken(String userId) {
        //generate a unique UUID
        log.trace("Generating new UUID for user {}", userId);
        String uuid = UUID.randomUUID().toString();
        String payload = userId+":"+uuid;

        //store the UUID and associate it with the user
        log.trace("Storing new UUID for user {}", userId);
        lock.acquireWrite();
        userIdToUUID.put(userId, uuid);
        lock.releaseWrite();
        return generateJwt(payload);
    }

    public boolean revokeAuthentication(String userId) {
        log.debug("Revoking authentication for user {}", userId);
        lock.acquireWrite();
        String uuid = userIdToUUID.remove(userId);
        lock.releaseWrite();
        return uuid != null;
    }

    public boolean validateToken(String userId, String token) {
        log.debug("Validating token for user {}", userId);
        boolean answer;
        try{
            String[] parts = new String(Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedContent(token)
                    .getPayload()).split(":");

            String uuidFromToken = parts[1];
            String userIdFromToken = parts[0];

            log.trace("Checking if the UUID from the token matches the stored UUID for user {}", userId);
            lock.acquireRead();
            String uuid = userIdToUUID.get(userId);
            lock.releaseRead();
            answer = uuid != null && uuid.equals(uuidFromToken) && userId.equals(userIdFromToken);
        } catch (Exception ignored) {
            answer = false;
        }
        log.debug("Token validation for user {} was {}", userId, answer ? "successful" : "unsuccessful");
        return answer;
    }

    /**
     * This operation logs out all users by resetting the secret key
     */
    public void resetSecretKey() {
        log.debug("Resetting secret key");
        lock.acquireWrite();
        key = Jwts.SIG.HS512.key().build();
        userIdToUUID.clear();
        lock.releaseWrite();
    }

    //================================================================================= |
    //=========================== AUTHENTICATION MANAGER ============================== |
    //================================================================================= |

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = loadUserByUsername(authentication.getName());
        String credentials = (String) authentication.getCredentials();
        if(authenticateUser(userDetails.getUsername(),credentials)) {
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } else {
            throw new AccessDeniedException("Bad credentials");
        }
    }

    //================================================================================= |
    //=========================== USER DETAILS MANAGER ================================ |
    //================================================================================= |

    @Override
    public void createUser(UserDetails user) {
        if(userExists(user.getUsername())){
            throw new AccessDeniedException("user already exists");
        }

        log.debug("Adding user credentials for user {}", user.getUsername());
        String hashedPassword = encoder.encode(user.getPassword());
        repository.saveHashedPassword(user.getUsername(),hashedPassword);
    }

    @Override
    public void updateUser(UserDetails user) {
        if(!userExists(user.getUsername())){
            throw new UsernameNotFoundException("User not found");
        }

        String hashedPassword = encoder.encode(user.getPassword());
        UserCredentials updatedUser = new UserCredentials(user.getUsername(), hashedPassword);
        repository.save(updatedUser);
    }

    @Override
    public void deleteUser(String username) {
        if(!userExists(username)){
            throw new UsernameNotFoundException("User not found");
        }

        repository.deleteById(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
        } else {
            String username = currentUser.getName();                                            //TODO: add this when we have a mongodb
            UserCredentials currentUserFromDB = repository.findById(username);  //.orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if(isPasswordsMatch(oldPassword, currentUserFromDB.getPassword())) {
                String hashedPassword = encoder.encode(newPassword);
                UserCredentials updatedUser = new UserCredentials(username, hashedPassword);
                repository.save(updatedUser);
            } else {
                throw new AccessDeniedException("Can't change password as old password is incorrect.");
            }
        }
    }

    @Override
    public boolean userExists(String username) {
        return repository.existsById(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!userExists(username)) {
            throw new UsernameNotFoundException("User not found");
        }
        return repository.findById(username);
    }

    //============================================================================ |
    //========================= PRIVATE METHODS ================================== |
    //============================================================================ |

    private boolean authenticateUser(String userId, String password) {
        log.debug("Authenticating user: {}", userId);
        String hashedPassword = repository.getHashedPassword(userId);

        // check if the user exists
        if(hashedPassword == null) {
            log.debug("User {} does not exist", userId);
            return false;
        }
        log.trace("User {} exists", userId);

        // check if the password is correct
        if(! isPasswordsMatch(password, hashedPassword)) {
            log.debug("Incorrect password for user {}", userId);
            return false;
        }

        log.debug("User {} authenticated successfully", userId);
        return true;
    }

    private boolean isPasswordsMatch(String password, String hashedPassword) {
        return encoder.matches(password, hashedPassword);
    }

    private String generateJwt(String payload) {
        log.debug("Generating token for user {}", payload);
        return Jwts.builder()
                .content(payload, "text/plain")
                .signWith(key,alg)
                .compact();
    }
}
