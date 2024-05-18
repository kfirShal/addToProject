package com.amazonas.business.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

@Component
public class AuthenticationController {

    //=================================================================
    //TODO: replace this with a database
    //Temporary storage for hashed passwords until we have a database
    private final Map<String, String> userIdToHashedPassword = new HashMap<>();
    //=================================================================

    private static final MacAlgorithm alg = Jwts.SIG.HS512;
    private static final String passwordStorageFormat = "{bcrypt}";

    private final Map<String, String> userIdToUUID;
    private final HashSet<String> uuids;
    private final Semaphore lock;

    private final PasswordEncoder encoder;
    private SecretKey key;

    public AuthenticationController() {
        key = Jwts.SIG.HS512.key().build();
        userIdToUUID = new HashMap<>();
        uuids = new HashSet<>();
        encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        lock = new Semaphore(1,true);
    }

    public AuthenticationResponse authenticate(String userId, String password) {

        //get the hashed password and UUID for the user
        lockAcquire();
        String hashedPassword = userIdToHashedPassword.get(userId);
        String uuid = userIdToUUID.get(userId);
        lock.release();

        // check if the user exists
        if(hashedPassword == null) {
            return new AuthenticationResponse(false, null);
        }

        // check if the password is correct
        boolean passwordsMatch = encoder.matches(
                passwordStorageFormat+password,
                hashedPassword); //TODO: get hashed password from database
        if(!passwordsMatch) {
            return new AuthenticationResponse(false, null);
        }

        lockAcquire();
        //remove the old UUID if it exists
        if(uuid != null) {
            uuids.remove(uuid);
        }

        //generate a unique UUID
        do{
            uuid = UUID.randomUUID().toString();
        } while (uuids.contains(uuid));

        //store the UUID and associate it with the user
        userIdToUUID.put(userId, uuid);
        uuids.add(uuid);
        lock.release();

        String token = generateToken(uuid);
        return new AuthenticationResponse(true,token);
    }

    public void revokeAuthentication(String userId) {
        lockAcquire();
        String uuid = userIdToUUID.remove(userId);
        uuids.remove(uuid);
        lock.release();
    }

    public boolean validateToken(String userId, String token) {
        boolean answer;
        try{
            String uuidFromToken = new String(Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedContent(token)
                    .getPayload());

            lockAcquire();
            String uuid = userIdToUUID.get(userId);
            answer = uuid != null && uuid.equals(uuidFromToken);
            lock.release();
        } catch (Exception ignored) {
            answer = false;
        }
        return answer;
    }

    /**
     * This operation logs out all users by resetting the secret key
     */
    public void resetSecretKey() {
        lockAcquire();
        key = Jwts.SIG.HS512.key().build();
        userIdToUUID.clear();
        uuids.clear();
        lock.release();
    }

    private void lockAcquire() {
        try {
            lock.acquire();
        } catch (InterruptedException ignored) {}
    }

    private String generateToken(String userId) {
        return Jwts.builder()
                .content(userId, "text/plain")
                .signWith(key,alg)
                .compact();
    }

    /**
     * Temporary method to add user credentials until we have a database
     */
    public void addUserCredentials(String userId, String password) {
        //TODO: store hashed password in database
        String encodedPassword = encoder.encode(passwordStorageFormat+password);
        lockAcquire();
        userIdToHashedPassword.put(userId, encodedPassword);
        lock.release();
    }
}
