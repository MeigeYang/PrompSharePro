package com.example.promptsharepro.model;

public class User {
    private String username;
    private String email;
    private String uscID;
    private String password;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    // Constructor with parameters
    public User(String username, String email, String uscID, String password) {
        this.username = username;
        this.email = email;
        this.uscID = uscID;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUscID() { return uscID; }
    public void setUscID(String uscID) { this.uscID = uscID; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

