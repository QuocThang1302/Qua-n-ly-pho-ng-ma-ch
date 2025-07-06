package com.example.model;

public class UserContext {
    private static UserContext instance;
    private String userId;
    private String username;
    private Role role;

    private UserContext() {}

    public static UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    public void setUser(String userId, String username, Role role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public void clear() {
        userId = null;
        username = null;
        role = null;
    }
}
