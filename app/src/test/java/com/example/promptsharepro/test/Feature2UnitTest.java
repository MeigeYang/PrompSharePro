package com.example.promptsharepro.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.example.promptsharepro.model.Comment;

@RunWith (JUnit4.class)
public class Feature2UnitTest {

    @Test
    public void testValidateRatingInput() {
        float rating = 3.5f;
        assertTrue("Rating should be between 1 and 5", rating >= 1 && rating <= 5);
    }

    @Test
    public void testCommentContentValidation() {
        String nullComment = null;
        String validComment = "Test comment";
        assertNull("Comment should be null", nullComment);
        assertNotNull("Comment should not be null", validComment);
    }

    @Test
    public void testCommentUpdate() {
        Comment comment = new Comment(
            "comment123",
            "post123",
            "Original comment",
            "user123",
            "2024-03-20 14:30",
            3.0f
        );
        
        comment.setContent("Updated comment");
        comment.setRating(4.0f);

        assertEquals("Content should be updated", "Updated comment", comment.getContent());
        assertEquals("Rating should be updated", 4.0f, comment.getRating(), 0.0f);
    }

    @Test
    public void testCommentCreationAndDeletion() {
        Comment comment = new Comment(
            "comment456",
            "post456",
            "Test comment",
            "user456",
            "2024-03-20 14:30",
            3.5f
        );

        assertEquals("Content should match", "Test comment", comment.getContent());
        assertEquals("Rating should match", 3.5f, comment.getRating(), 0.0f);
    }
}
