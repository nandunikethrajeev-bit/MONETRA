package com.monetra.desktop.service;

import com.monetra.core.dto.UserDto;

public class SessionManager {

    private static SessionManager instance;

    private UserDto currentUser;
    private String authToken;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(UserDto user, String token) {
        this.currentUser = user;
        this.authToken = token;
    }

    public void logout() {
        this.currentUser = null;
        this.authToken = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public UserDto getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserDto user) {
        this.currentUser = user;
    }

    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    public String getAuthToken() {
        return authToken;
    }
}

