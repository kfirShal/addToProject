package com.amazonas.frontend.model;

public record User(String username, String password) {

    public User() {
        this("", "");
    }
}
