package com.amazonas.backend.business.authentication;

public record UserCredentials(String userId, String hashedPassword) {
}
