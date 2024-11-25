package com.example.promptsharepro.test;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.promptsharepro.LoginActivity;
import com.example.promptsharepro.MainActivity;
import com.example.promptsharepro.ProfileActivity;
import com.example.promptsharepro.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

/**
 * UI tests for the Profile feature using Espresso.
 * This test performs the following steps:
 * 1. Logs in with test user credentials.
 * 2. Navigates to the Profile page.
 * 3. Edits the user ID (acting as username).
 * 4. Verifies the user ID update.
 */
@RunWith(AndroidJUnit4.class)
public class ProfileUITest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Performs login with the provided email and password.
     *
     * @param email    The email address to input.
     * @param password The password to input.
     */
    private void performLogin(String email, String password) {
        // Enter email
        onView(withId(R.id.etLoginEmail))
                .perform(typeText(email), closeSoftKeyboard());

        // Enter password
        onView(withId(R.id.etLoginPassword))
                .perform(typeText(password), closeSoftKeyboard());

        // Click the login button
        onView(withId(R.id.btnLogin))
                .perform(click());

        // Verify that MainActivity is displayed by checking the presence of a unique view
        try {
            onView(withId(R.id.btnProfile))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            throw new AssertionError("MainActivity not displayed after login. Ensure btnProfile exists in MainActivity.");
        }
    }

    /**
     * Tests editing the profile user ID.
     */
    @Test
    public void testEditProfileUserId() {
        // Step 1: Perform login with valid credentials
        performLogin("test@usc.edu", "testtest");

        // Step 2: Navigate to ProfileActivity by clicking the Profile button
        onView(withId(R.id.btnProfile))
                .perform(click());

        // Verify that ProfileActivity is displayed by checking the presence of etId
        try {
            onView(withId(R.id.etId))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            throw new AssertionError("ProfileActivity not displayed. Ensure etId exists in ProfileActivity.");
        }

        // Step 3: Click the edit user ID button
        onView(withId(R.id.btnEditId))
                .perform(click());

        // Step 4: Clear the user ID field and enter a new user ID
        String newUserId = "7608159078";
        onView(withId(R.id.etId))
                .perform(clearText(), typeText(newUserId), closeSoftKeyboard());

        // Step 5: Click the save button
        onView(withId(R.id.btnEditId))
                .perform(click());

        // Step 6: Verify that the user ID has been updated
        onView(withId(R.id.etId))
                .check(matches(withText(newUserId)));
    }

    @Before
    public void setUp() {
        // Initialize any resources before tests run
        // For example, you can set up mock data or initialize Idling Resources here
    }

    @After
    public void tearDown() {
        // Clean up resources after tests run
        // For example, you can unregister Idling Resources here
    }
}
