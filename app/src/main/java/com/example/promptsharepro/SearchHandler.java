package com.example.promptsharepro;

import com.example.promptsharepro.model.Post;
import java.util.ArrayList;
import java.util.List;

public class SearchHandler {
    private List<Post> allPosts;

    public SearchHandler(List<Post> posts) {
        this.allPosts = posts != null ? posts : new ArrayList<>();
    }

    public List<Post> searchByLLMKind(String llmKind) {
        if (llmKind == null || llmKind.trim().isEmpty()) {
            return allPosts;
        }

        List<Post> results = new ArrayList<>();
        String searchTerm = llmKind.toLowerCase().trim();

        for (Post post : allPosts) {
            if (post.getLLMKind() != null && 
                post.getLLMKind().toLowerCase().contains(searchTerm)) {
                results.add(post);
            }
        }
        return results;
    }

    public List<Post> searchByTitle(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return allPosts;
        }

        List<Post> results = new ArrayList<>();
        String searchTerm = keyword.toLowerCase().trim();

        for (Post post : allPosts) {
            if (post.getTitle() != null && 
                post.getTitle().toLowerCase().contains(searchTerm)) {
                results.add(post);
            }
        }
        return results;
    }

    public List<Post> fullTextSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return allPosts;
        }

        List<Post> results = new ArrayList<>();
        String searchTerm = keyword.toLowerCase().trim();

        for (Post post : allPosts) {
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

    public void updatePosts(List<Post> newPosts) {
        this.allPosts = newPosts != null ? newPosts : new ArrayList<>();
    }
} 