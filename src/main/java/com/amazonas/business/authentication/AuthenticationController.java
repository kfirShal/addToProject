package com.amazonas.business.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationController {

    //=================================================================
    //TODO: replace this with a database
    //Temporary storage for hashed passwords until we have a database
    private final Map<String, String> userIdToHashedPassword = new HashMap<>();
    //=================================================================

    private static final MacAlgorithm alg = Jwts.SIG.HS512;
    private static final String passwordStorageFormat = "{bcrypt}";
    private final PasswordEncoder encoder;
    private SecretKey key;

    public AuthenticationController() {
        key = Jwts.SIG.HS512.key().build();
        encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Temporary method to add user credentials until we have a database
     */
    public void addUserCredentials(String userId, String password) {
        //TODO: store hashed password in database
        String encodedPassword = encoder.encode(passwordStorageFormat+password);
        userIdToHashedPassword.put(userId, encodedPassword);
    }

    public AuthenticationResponse authenticate(String userId, String password) {

        boolean passwordsMatch = encoder.matches(
                passwordStorageFormat+password,
                userIdToHashedPassword.get(userId)); //TODO: get hashed password from database

        if(!passwordsMatch) {
            return new AuthenticationResponse(false, null);
        }

        //password is valid
        String token = generateToken(userId);
        return new AuthenticationResponse(true,token);
    }

    private String generateToken(String userId) {
        return Jwts.builder()
                .content(userId, "text/plain")
                .signWith(key,alg)
                .compact();
    }

    public boolean validateToken(String userId, String token) {
        try{
            byte[] userIdBytes = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedContent(token)
                    .getPayload();
            String userIdFromToken = new String(userIdBytes);

            return userId.equals(userIdFromToken);
        } catch (Exception e) {
            return false;
        }
    }

    public void resetSecretKey() {
        key = Jwts.SIG.HS512.key().build();
    }
}
