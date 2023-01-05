package com.nconnect.usersupport.infrastructure.resource;

public class AuthenticationError {
    private final String message;

    public AuthenticationError(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

}
