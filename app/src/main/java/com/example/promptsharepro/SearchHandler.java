package com.example.promptsharepro;

import com.example.promptsharepro.model.Post;
import java.util.ArrayList;
import java.util.List;

public class SearchHandler {
    private List<Post> posts;

    public SearchHandler(List<Post> posts) {
        this.posts = posts;
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts = newPosts;
    }

    public List<Post> searchByLLMKind(String query) {
        List<Post> results = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase();

        for (Post post : posts) {
            if (post.getLlmKind() != null && 
                post.getLlmKind().toLowerCase().contains(lowercaseQuery)) {
                results.add(post);
            }
        }
        return results;
    }

    public List<Post> searchByTitle(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return posts;
        }

        List<Post> results = new ArrayList<>();
        String searchTerm = keyword.toLowerCase().trim();

        for (Post post : posts) {
            if (post.getTitle() != null && 
                post.getTitle().toLowerCase().contains(searchTerm)) {
                results.add(post);
            }
        }
        return results;
    }

    public List<Post> fullTextSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return posts;
        }

        List<Post> results = new ArrayList<>();
        String searchTerm = keyword.toLowerCase().trim();

        for (Post post : posts) {
            if ((post.getTitle() != null && 
                 post.getTitle().toLowerCase().contains(searchTerm)) ||
                (post.getContent() != null && 
                 post.getContent().toLowerCase().contains(searchTerm)) ||
                (post.getAuthorNotes() != null && 
                 post.getAuthorNotes().toLowerCase().contains(searchTerm))) {
                results.add(post);
            }
        }
        return results;
    }
} 