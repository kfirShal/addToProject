package com.amazonas.backend.service.requests.auth;

public record AuthenticationRequest (String userId, String password) {
}
