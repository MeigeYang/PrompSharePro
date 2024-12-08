package com.example.promptsharepro.model;

import java.util.Map;

public class Favorite {
    private Map<String, String> favoritePostIDs;

    // Default constructor required for Firebase deserialization
    public Favorite() {}

    public Map<String, String> getFavoritePostIDs() {
        return favoritePostIDs;
    }

    public void setFavoritePostIDs(Map<String, String> favoritePostIDs) {
        this.favoritePostIDs = favoritePostIDs;
    }

    /**
     * Removes a post ID from the favorites.
     * @param postID The ID of the post to remove.
     */
    public void removeFavoritePostID(String postID) {
        if (favoritePostIDs != null) {
            favoritePostIDs.remove(postID);
        }
    }

    /**
     * Adds a post ID to the favorites.
     * @param postID The ID of the post to add.
     */
    public void addFavoritePostID(String postID) {
        if (favoritePostIDs != null) {
            favoritePostIDs.put(postID, postID);
        }
    }
} 