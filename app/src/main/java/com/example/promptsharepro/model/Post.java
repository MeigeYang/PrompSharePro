package com.example.promptsharepro.model;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private String postID;
    private String title;
    private String content;
    private String timestamp;
    private String llmKind;
    private String createdBy; // id of user who create this Event
    private List<Comment> comments;  // List of comments associated with the event
    private String authorNotes;
    private boolean favorited;  // Add this field


    // Constructor
    public Post(String postID, String title, String content, String timestamp, String createdBy, String llmKind) {
        this.postID = postID;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.createdBy = createdBy;
        this.llmKind = llmKind;
        this.comments = new ArrayList<>();  // Initialize the list of comments
    }

    // Required empty constructor for Firebase
    public Post() {
        // Required empty constructor for Firebase
    }

    // Getters and Setters
    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLlmKind() {
        return llmKind != null ? llmKind : "Unknown";
    }

    public void setLlmKind(String llmKind) {
        this.llmKind = llmKind;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getAuthorNotes() {
        return authorNotes;
    }

    public void setAuthorNotes(String authorNotes) {
        this.authorNotes = authorNotes;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }



    // Add a comment to the list
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    // Remove a comment from the list
    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }

    @Override
    public String toString() {
        return "Post{" +
            "postID='" + postID + '\'' +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", timestamp='" + timestamp + '\'' +
            ", llmKind='" + llmKind + '\'' +
            ", createdBy='" + createdBy + '\'' +
            ", authorNotes='" + authorNotes + '\'' +
            '}';
    }
}
