package com.amazonas.backend.business.authentication;

import com.amazonas.common.utils.ReadWriteLock;
import com.amazonas.backend.repository.UserCredentialsRepository;
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
import java.util.UUID;

@Component
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserCredentialsRepository repository;

    private static final MacAlgorithm alg = Jwts.SIG.HS512;
    private static final String passwordStorageFormat = "{bcrypt}";
    private final Map<String, String> userIdToUUID;
    private final PasswordEncoder encoder;
    private final ReadWriteLock lock;
    private SecretKey key;

    public AuthenticationController(UserCredentialsRepository userCredentialsRepository) {
        this.repository = userCredentialsRepository;
        key = Jwts.SIG.HS512.key().build();
        userIdToUUID = new HashMap<>();
        lock = new ReadWriteLock();
        encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public AuthenticationResponse authenticateGuest(String userid){
        log.debug("Generating token for guest user {}", userid);
        return new AuthenticationResponse(true, getToken(userid));
    }

    public AuthenticationResponse authenticateUser(String userId, String password) {
        log.debug("Authenticating user: {}", userId);
        String hashedPassword = repository.getHashedPassword(userId);

        // check if the user exists
        if(hashedPassword == null) {
            log.debug("User {} does not exist", userId);
            return new AuthenticationResponse(false, null);
        }
        log.trace("User {} exists", userId);

        // check if the password is correct
        if(! isPasswordsMatch(password, hashedPassword)) {
            log.debug("Incorrect password for user {}", userId);
            return new AuthenticationResponse(false, null);
        }

        String token = getToken(userId);
        log.debug("User {} authenticated successfully", userId);
        return new AuthenticationResponse(true,token);
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
            String uuidFromToken = new String(Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedContent(token)
                    .getPayload());

            log.trace("Checking if the UUID from the token matches the stored UUID for user {}", userId);
            lock.acquireRead();
            String uuid = userIdToUUID.get(userId);
            lock.releaseRead();
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
        lock.acquireWrite();
        key = Jwts.SIG.HS512.key().build();
        userIdToUUID.clear();
        lock.releaseWrite();
    }

    private boolean isPasswordsMatch(String password, String hashedPassword) {
        return encoder.matches(passwordStorageFormat+ password, hashedPassword);
    }

    private String getToken(String userId) {
        //generate a unique UUID
        log.trace("Generating new UUID for user {}", userId);
        String uuid = UUID.randomUUID().toString();

        //store the UUID and associate it with the user
        log.trace("Storing new UUID for user {}", userId);
        lock.acquireWrite();
        userIdToUUID.put(userId, uuid);
        lock.releaseWrite();

        return generateJwt(uuid);
    }

    private String generateJwt(String payload) {
        log.debug("Generating token for user {}", payload);
        return Jwts.builder()
                .content(payload, "text/plain")
                .signWith(key,alg)
                .compact();
    }

    public void addUserCredentials(String userId, String password) {
        log.debug("Adding user credentials for user {}", userId);
        String encodedPassword = encoder.encode(passwordStorageFormat+password);
        repository.saveHashedPassword(userId, encodedPassword);
    }
}
