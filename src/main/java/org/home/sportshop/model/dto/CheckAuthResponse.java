package org.home.sportshop.model.dto;

public class CheckAuthResponse {
    private boolean authenticated;
    private AuthResponse.UserDto user;

    public CheckAuthResponse() {
    }

    public CheckAuthResponse(boolean authenticated, AuthResponse.UserDto user) {
        this.authenticated = authenticated;
        this.user = user;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public AuthResponse.UserDto getUser() {
        return user;
    }

    public void setUser(AuthResponse.UserDto user) {
        this.user = user;
    }
} 