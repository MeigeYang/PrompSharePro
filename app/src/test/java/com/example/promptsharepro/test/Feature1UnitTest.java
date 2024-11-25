package com.example.promptsharepro.test;

import static org.junit.Assert.assertEquals;

import com.example.promptsharepro.model.Post;
import com.example.promptsharepro.model.Comment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class Feature1UnitTest {

    @Test
    public void testPostCreation() {

        Post post = new Post("1", "Test Post", "This is a test post", "2024-01-01", "user1", "Other");
        post.setAuthorNotes("This is the author notes");

        assertEquals("PostID should match", "1", post.getPostID());
        assertEquals("Title should match", "Test Post", post.getTitle());
        assertEquals("Content should match", "This is a test post", post.getContent());
        assertEquals("Timestamp should match", "2024-01-01", post.getTimestamp());
        assertEquals("User should match", "user1", post.getCreatedBy());
        assertEquals("LLM Kind should match", "Other", post.getLlmKind());
        assertEquals("Author Notes should match", "This is the author notes", post.getAuthorNotes());
    }

    @Test
    public void testPostUpdate() {
        Post post = new Post("1", "Test Post", "This is a test post", "2024-01-01", "user1", "Other");
        post.setAuthorNotes("This is the author notes");

        post.setTitle("Updated Post Title");
        post.setContent("Updated Post Content");
        post.setLlmKind("Llama");
        post.setAuthorNotes("Updated Author Notes");

        assertEquals("Title should match", "Updated Post Title", post.getTitle());
        assertEquals("Content should match", "Updated Post Content", post.getContent());
        assertEquals("LLM Kind should match", "Llama", post.getLlmKind());
        assertEquals("Author Notes should match", "Updated Author Notes", post.getAuthorNotes());
    }

    @Test
    public void testPostComments() {
        Post post = new Post("1", "Test Post", "This is a test post", "2024-01-01", "user1", "Other");
        Comment comment1 = new Comment("1", "1", "comment1", "user2", "2024-01-01", 1.5f);
        Comment comment2 = new Comment("2", "1", "comment2", "user3", "2024-01-01", 2f);
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);
        post.setComments(comments);

        assertEquals("Should have 2 comments", 2, post.getComments().size());
    }

    @Test
    public void testPostAddComment() {
        Post post = new Post("1", "Test Post", "This is a test post", "2024-01-01", "user1", "Other");
        Comment comment1 = new Comment("1", "1", "comment1", "user2", "2024-01-01", 1.5f);
        post.addComment(comment1);

        assertEquals("Should have 1 comment", 1, post.getComments().size());
    }

    @Test
    public void testPostRemoveComment() {
        Post post = new Post("1", "Test Post", "This is a test post", "2024-01-01", "user1", "Other");
        Comment comment1 = new Comment("1", "1", "comment1", "user2", "2024-01-01", 1.5f);
        Comment comment2 = new Comment("2", "1", "comment2", "user3", "2024-01-01", 2f);
        post.addComment(comment1);
        post.addComment(comment2);
        post.removeComment(comment1);

        assertEquals("Should have 1 comment", 1, post.getComments().size());
    }
}
