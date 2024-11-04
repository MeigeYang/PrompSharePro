package com.example.promptsharepro.model;

public class User {
    private String username;
    private String email;
    private String uscID;

    // Constructor
    public User(String username, String email, String uscID) {
        this.username = username;
        this.email = email;
        this.uscID = uscID;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUscID() { return uscID; }
    public void setUscID(String uscID) { this.uscID = uscID; }


}

