package com.amazonas.backend.service;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.authentication.AuthenticationResponse;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.auth.AuthenticationRequest;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

@Component("authenticationService")
public class AuthenticationService {

    private final AuthenticationController controller;

    public AuthenticationService(AuthenticationController controller) {
        this.controller = controller;
    }

    public String authenticateUser(String json) {
        Request request = Request.from(json);
        AuthenticationRequest authReq = JsonUtils.deserialize(request.payload(), AuthenticationRequest.class);
        AuthenticationResponse authResp = controller.authenticateUser(authReq.userId(), authReq.password());
        String data = authResp.success() ? authResp.token() : null;
        String message = authResp.success() ? "Authentication successful" : "Authentication failed";
        return new Response(message,authResp.success(), data).toJson();
    }

    public String authenticateGuest(String json) {
        Request request = Request.from(json);
        AuthenticationRequest authReq = JsonUtils.deserialize(request.payload(), AuthenticationRequest.class);
        AuthenticationResponse authResp = controller.authenticateGuest(authReq.userId());
        String data = authResp.success() ? authResp.token() : null;
        String message = authResp.success() ? "Authentication successful" : "Authentication failed";
        return new Response(message,authResp.success(), data).toJson();
    }
}
