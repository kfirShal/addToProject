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
    private final PasswordEncoder encoder;
    private SecretKey key;

    public AuthenticationController() {
        key = Jwts.SIG.HS512.key().build();
        userIdToUUID = new HashMap<>();
        uuids = new HashSet<>();
        encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public AuthenticationResponse authenticate(String userId, String password) {

        // check if the user exists
        if(! userIdToHashedPassword.containsKey(userId)) {
            return new AuthenticationResponse(false, null);
        }

        // check if the password is correct
        boolean passwordsMatch = encoder.matches(
                passwordStorageFormat+password,
                userIdToHashedPassword.get(userId)); //TODO: get hashed password from database
        if(!passwordsMatch) {
            return new AuthenticationResponse(false, null);
        }

        //revoke any existing authentication for this user if it exists
        //this is to prevent multiple logins
        if(userIdToUUID.containsKey(userId)) {
            revokeAuthentication(userId);
        }

        //generate a unique UUID
        String uuid;
        do{
            uuid = UUID.randomUUID().toString();
        } while (uuids.contains(uuid));

        //store the UUID and associate it with the user
        uuids.add(uuid);
        userIdToUUID.put(userId, uuid);
        String token = generateToken(uuid);

        return new AuthenticationResponse(true,token);
    }

    public void revokeAuthentication(String userId) {
        String uuid = userIdToUUID.remove(userId);
        uuids.remove(uuid);
    }

    public boolean validateToken(String userId, String token) {
        try{
            String uuidFromToken = new String(Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedContent(token)
                    .getPayload());

            return userIdToUUID.get(userId).equals(uuidFromToken);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This operation logs out all users by resetting the secret key
     */
    public void resetSecretKey() {
        key = Jwts.SIG.HS512.key().build();
        userIdToUUID.clear();
        uuids.clear();
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
        userIdToHashedPassword.put(userId, encodedPassword);
    }
}
