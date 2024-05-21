package com.amazonas.business.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    //=================================================================
    //TODO: replace this with a database
    //Temporary storage for hashed passwords until we have a database
    private final Map<String, String> userIdToHashedPassword = new HashMap<>();
    //=================================================================

    private static final MacAlgorithm alg = Jwts.SIG.HS512;
    private static final String passwordStorageFormat = "{bcrypt}";

    private final ConcurrentMap<String, String> userIdToUUID;
    private final Set<String> uuids;

    private final PasswordEncoder encoder;
    private SecretKey key;

    public AuthenticationController() {
        key = Jwts.SIG.HS512.key().build();
        userIdToUUID = new ConcurrentHashMap<>();
        uuids = ConcurrentHashMap.newKeySet();
        encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public AuthenticationResponse authenticate(String userId, String password) {
        log.debug("Authenticating user: {}", userId);

        String hashedPassword = userIdToHashedPassword.get(userId);
        String uuid = userIdToUUID.get(userId);

        // check if the user exists
        if(hashedPassword == null) {
            log.debug("User {} does not exist", userId);
            return new AuthenticationResponse(false, null);
        }
        log.trace("User {} exists", userId);

        // check if the password is correct
        boolean passwordsMatch = encoder.matches(
                passwordStorageFormat+password,
                hashedPassword); //TODO: get hashed password from database
        if(!passwordsMatch) {
            log.debug("Incorrect password for user {}", userId);
            return new AuthenticationResponse(false, null);
        }

        //remove the old UUID if it exists
        if(uuid != null) {
            log.trace("Removing old UUID for user {}", userId);
            uuids.remove(uuid);
        }

        //generate a unique UUID
        log.trace("Generating new UUID for user {}", userId);
        do{
            uuid = UUID.randomUUID().toString();
        } while (uuids.contains(uuid));

        //store the UUID and associate it with the user
        log.trace("Storing new UUID for user {}", userId);
        userIdToUUID.put(userId, uuid);
        uuids.add(uuid);

        String token = generateToken(uuid);
        log.debug("User {} authenticated successfully", userId);
        return new AuthenticationResponse(true,token);
    }

    public boolean revokeAuthentication(String userId) {
        log.debug("Revoking authentication for user {}", userId);
        String uuid = userIdToUUID.remove(userId);
        uuids.remove(uuid);
        return uuid != null;
    }

    public boolean validateToken(String userId, String token) {
        log.debug("Validating token for user {}", userId);
        boolean answer;
        try{
            String uuidFromToken = new String(Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedContent(token)
                    .getPayload());

            log.trace("Checking if the UUID from the token matches the stored UUID for user {}", userId);
            String uuid = userIdToUUID.get(userId);
            answer = uuid != null && uuid.equals(uuidFromToken);
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
        key = Jwts.SIG.HS512.key().build();
        userIdToUUID.clear();
        uuids.clear();
    }

    private String generateToken(String userId) {
        log.debug("Generating token for user {}", userId);
        return Jwts.builder()
                .content(userId, "text/plain")
                .signWith(key,alg)
                .compact();
    }

    /**
     * Temporary method to add user credentials until we have a database
     */
    public void addUserCredentials(String userId, String password) {
        log.debug("Adding user credentials for user {}", userId);
        //TODO: store hashed password in database
        String encodedPassword = encoder.encode(passwordStorageFormat+password);
        userIdToHashedPassword.put(userId, encodedPassword);
    }
}
