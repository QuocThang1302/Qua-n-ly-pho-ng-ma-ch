package com.example.model;
public class UserContext {
    private static UserContext instance;
    private String username;
    private Role role;

    private UserContext() {}

    public static UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    public void setUser(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public void clear() {
        username = null;
        role = null;
    }
}
