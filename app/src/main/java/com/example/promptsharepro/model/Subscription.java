package com.example.promptsharepro.model;

public class Subscription {
    private String subscriptionId;
    private String llmType;
    private String userName;
    private String email;
    private String timestamp;

    // Required empty constructor for Firebase
    public Subscription() {}

    public Subscription(String subscriptionId, String llmType, String userName, String email, String timestamp) {
        this.subscriptionId = subscriptionId;
        this.llmType = llmType;
        this.userName = userName;
        this.email = email;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }

    public String getLlmType() { return llmType; }
    public void setLlmType(String llmType) { this.llmType = llmType; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
} 