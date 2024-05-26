package com.amazonas.service;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.authentication.AuthenticationResponse;
import com.amazonas.utils.Response;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {

    private final AuthenticationController controller;

    public AuthenticationService(AuthenticationController controller) {
        this.controller = controller;
    }

    public String authenticate(String username, String password) {
        AuthenticationResponse authResp = controller.authenticateUser(username, password);
        String data = authResp.success() ? authResp.token() : "";
        String message = authResp.success() ? "Authentication successful" : "Authentication failed";
        return new Response(message,authResp.success(), data).toJson();
    }
}
