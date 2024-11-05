package com.example.promptsharepro.model;
import java.time.LocalDateTime;

public class Comment {
    private String commentID;
    private String postID;  // ID of the post or event the comment is associated with
    private String content;
    private String createdBy;
    private String timestamp;
    private float rating;

    // Constructor
    public Comment() {
        // Required empty constructor for Firebase
    }

    public Comment(String commentID, String postID, String content, String createdBy, String timestamp, float rating) {
        this.commentID = commentID;
        this.postID = postID;
        this.content = content;
        this.createdBy = createdBy;
        this.timestamp = timestamp;
        this.rating = rating;
    }

    // Getters and Setters
    public String getCommentID() { return commentID; }
    public void setCommentID(String commentID) { this.commentID = commentID; }

    public String getPostID() { return postID; }
    public void setPostID(String postID) { this.postID = postID; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    // New method to set timestamp using LocalDateTime
    public void setTimestampFromDateTime(LocalDateTime dateTime) {
        this.timestamp = dateTime.toString();
    }
}
