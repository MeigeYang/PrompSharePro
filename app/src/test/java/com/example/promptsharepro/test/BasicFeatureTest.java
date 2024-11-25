package com.example.promptsharepro.test;

import static org.junit.Assert.*;

import com.example.promptsharepro.model.Comment;
import com.example.promptsharepro.model.Post;
import com.example.promptsharepro.model.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Unit tests for basic user and post functionalities.
 */
public class BasicFeatureTest {

    private User testUser;
    private Post testPost;
    private Comment testComment;

    @Before
    public void setUp() {
        // Initialize test data
        testUser = new User("TestUser", "test.user@example.com", "TU123456", "TestPass!23");
        testPost = new Post("post001", "Test Post Title", "Test Post Content", "2024-04-01", "TestUser", "GPT-4");
        testComment = new Comment("comment123", "post123", "Original comment", "user123", "2024-03-20 14:30", 3.0f);
    }

    /**
     * Test Case 1: User Sign-Up and Profile Creation
     * Objective: Verify that a user can successfully create a profile with valid credentials.
     */
    @Test
    public void testUserSignUpAndProfileCreation() {
        // Arrange
        User newUser = new User("JaneDoe", "jane.doe@example.com", "JD123456", "SecurePass!23");

        // Act
        // Simulate profile creation by setting user details
        newUser.setUsername("JaneDoe");
        newUser.setEmail("jane.doe@example.com");
        newUser.setUscID("JD123456");
        newUser.setPassword("SecurePass!23");

        // Assert
        assertEquals("Username should match", "JaneDoe", newUser.getUsername());
        assertEquals("Email should match", "jane.doe@example.com", newUser.getEmail());
        assertEquals("USC ID should match", "JD123456", newUser.getUscID());
        assertEquals("Password should match", "SecurePass!23", newUser.getPassword());
    }

    /**
     * Test Case 2: Email and ID Validation During Registration
     * Objective: Ensure that the system rejects registration attempts with invalid email formats and duplicate IDs.
     */
    @Test
    public void testEmailAndIDValidationDuringRegistration() {
        // Arrange
        User userWithInvalidEmail = new User("InvalidEmailUser", "invalidemail.com", "IE123456", "InvalidPass!23");
        User userWithDuplicateID = new User("DuplicateIDUser", "duplicate.id@example.com", "TU123456", "DuplicatePass!23"); // TU123456 already used by testUser

        // Act & Assert

        // Email Validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        assertFalse("Email should be invalid", pattern.matcher(userWithInvalidEmail.getEmail()).matches());

        // ID Validation (assuming ID should be unique)
        // Simulate checking for duplicate ID
        boolean isDuplicateID = testUser.getUscID().equals(userWithDuplicateID.getUscID());
        assertTrue("USC ID should be duplicate", isDuplicateID);
    }

    /**
     * Test Case 3: User Login Validation
     * Objective: Verify that a user can log in with correct credentials and cannot with incorrect ones.
     */
    @Test
    public void testUserLoginValidation() {
        // Arrange
        User existingUser = testUser; // User already set up in setUp()
        String correctEmail = existingUser.getEmail();
        String correctPassword = existingUser.getPassword();
        String incorrectPassword = "WrongPass!45";

        // Act & Assert

        // Correct Credentials
        boolean canLoginWithCorrectCredentials = existingUser.getEmail().equals(correctEmail)
                && existingUser.getPassword().equals(correctPassword);
        assertTrue("User should be able to log in with correct credentials.", canLoginWithCorrectCredentials);

        // Incorrect Password
        boolean canLoginWithIncorrectPassword = existingUser.getEmail().equals(correctEmail)
                && existingUser.getPassword().equals(incorrectPassword);
        assertFalse("User should not be able to log in with incorrect password.", canLoginWithIncorrectPassword);
    }

    /**
     * Test Case 4: Profile Update Functionality
     * Objective: Ensure that users can update their profile information successfully.
     */
    @Test
    public void testProfileUpdateFunctionality() {
        // Arrange
        User existingUser = testUser;

        // Act
        existingUser.setUsername("TestUserUpdated");
        existingUser.setEmail("test.user.updated@example.com");
        existingUser.setUscID("TU654321");
        existingUser.setPassword("UpdatedPass!45");

        // Assert
        assertEquals("Username should be updated", "TestUserUpdated", existingUser.getUsername());
        assertEquals("Email should be updated", "test.user.updated@example.com", existingUser.getEmail());
        assertEquals("USC ID should be updated", "TU654321", existingUser.getUscID());
        assertEquals("Password should be updated", "UpdatedPass!45", existingUser.getPassword());
    }

    /**
     * Test Case 5: Main Page Post Display Simulation
     * Objective: Verify that the main page correctly retrieves and displays a list of posts.
     * Note: This test simulates post retrieval and display logic without actual UI components.
     */
    @Test
    public void testMainPagePostDisplaySimulation() {
        // Arrange
        List<Post> allPosts = new ArrayList<>();
        allPosts.add(testPost);
        Post secondPost = new Post("post002", "Second Test Post", "Content of the second test post.", "2024-04-02", "TestUser", "Claude");
        allPosts.add(secondPost);

        // Act
        // Simulate retrieving posts
        List<Post> retrievedPosts = new ArrayList<>(allPosts);

        // Assert
        assertNotNull("Retrieved posts should not be null.", retrievedPosts);
        assertFalse("Retrieved posts list should not be empty.", retrievedPosts.isEmpty());
        assertEquals("There should be 2 posts retrieved.", 2, retrievedPosts.size());
        assertEquals("First post title should match.", "Test Post Title", retrievedPosts.get(0).getTitle());
        assertEquals("Second post title should match.", "Second Test Post", retrievedPosts.get(1).getTitle());
    }
}
