package com.example.promptsharepro.test;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.promptsharepro.LoginActivity;
import com.example.promptsharepro.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

/**
 * SignUpUITest performs UI tests on the SignUp functionality of the application.
 */
@RunWith(AndroidJUnit4.class)
public class SignUpUITest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Helper method to navigate to SignUpActivity from LoginActivity.
     */
    private void navigateToSignUp() {
        onView(withId(R.id.tvSignUp))
                .perform(click());
    }

    /**
     * Test 1: Attempt to sign up with an invalid email (not ending with @usc.edu).
     * Expected Result: Display error message "Must be a USC email."
     */
    @Test
    public void testSignUpWithInvalidEmail() {
        navigateToSignUp();

        // Enter invalid email
        onView(withId(R.id.etSignUpEmail))
                .perform(typeText("invaliduser@gmail.com"), closeSoftKeyboard());

        // Enter valid USC ID
        onView(withId(R.id.etSignUpUSCid))
                .perform(typeText("1234567890"), closeSoftKeyboard());

        // Enter unique username
        onView(withId(R.id.etSignUpUserName))
                .perform(typeText("uniqueuser1"), closeSoftKeyboard());

        // Enter valid password
        onView(withId(R.id.etSignUpPassword))
                .perform(typeText("password123"), closeSoftKeyboard());

        // Click the sign-up button
        onView(withId(R.id.btnSignUp))
                .perform(click());

        // Verify that the appropriate error message is displayed
        onView(withId(R.id.signUpError))
                .check(matches(withText("Must be a USC email.")));
    }

    /**
     * Test 2: Attempt to sign up with an invalid USC ID (not exactly 10 digits).
     * Expected Result: Display error message "Invalid ID: Must be exactly 10 digits and contain only numbers."
     */
    @Test
    public void testSignUpWithInvalidUSCId() {
        navigateToSignUp();

        // Enter valid USC email
        onView(withId(R.id.etSignUpEmail))
                .perform(typeText("validuser@usc.edu"), closeSoftKeyboard());

        // Enter invalid USC ID (9 digits)
        onView(withId(R.id.etSignUpUSCid))
                .perform(typeText("123456789"), closeSoftKeyboard());

        // Enter unique username
        onView(withId(R.id.etSignUpUserName))
                .perform(typeText("uniqueuser2"), closeSoftKeyboard());

        // Enter valid password
        onView(withId(R.id.etSignUpPassword))
                .perform(typeText("password123"), closeSoftKeyboard());

        // Click the sign-up button
        onView(withId(R.id.btnSignUp))
                .perform(click());

        // Verify that the appropriate error message is displayed
        onView(withId(R.id.signUpError))
                .check(matches(withText("Invalid ID: Must be exactly 10 digits and contain only numbers.")));
    }

    /**
     * Test 3: Attempt to sign up with an existing email, USC ID, and username.
     * Expected Result: Display error message "Account already exists with this email."
     */
    @Test
    public void testSignUpWithExistingUser() {
        navigateToSignUp();

        // Enter existing email
        onView(withId(R.id.etSignUpEmail))
                .perform(typeText("test@usc.edu"), closeSoftKeyboard());

        // Enter existing USC ID
        onView(withId(R.id.etSignUpUSCid))
                .perform(typeText("0000000004"), closeSoftKeyboard());

        // Enter existing username
        onView(withId(R.id.etSignUpUserName))
                .perform(typeText("mark"), closeSoftKeyboard());

        // Enter valid password
        onView(withId(R.id.etSignUpPassword))
                .perform(typeText("testtest"), closeSoftKeyboard());

        // Click the sign-up button
        onView(withId(R.id.btnSignUp))
                .perform(click());

        // Verify that the appropriate error message is displayed
        onView(withId(R.id.signUpError))
                .check(matches(withText("Account already exists with this email.")));
    }

    /**
     * Test 4: Successfully register a new user with valid and unique credentials.
     * Expected Result: Navigation to MainActivity.
     */
    @Test
    public void testSuccessfulSignUp() {
        navigateToSignUp();

        // Generate unique email using timestamp
        String uniqueEmail = "newuser" + System.currentTimeMillis() + "@usc.edu";
        onView(withId(R.id.etSignUpEmail))
                .perform(typeText(uniqueEmail), closeSoftKeyboard());

        // Generate unique USC ID (ensure uniqueness in your Firebase)
        String uniqueUSCId = String.format("%010d", System.currentTimeMillis() % 10000000000L);// Modify as needed to ensure uniqueness
        onView(withId(R.id.etSignUpUSCid))
                .perform(typeText(uniqueUSCId), closeSoftKeyboard());

        // Generate unique username using timestamp
        String uniqueUsername = "uniqueuser" + System.currentTimeMillis();
        onView(withId(R.id.etSignUpUserName))
                .perform(typeText(uniqueUsername), closeSoftKeyboard());

        // Enter valid password
        String validPassword = "password123";
        onView(withId(R.id.etSignUpPassword))
                .perform(typeText(validPassword), closeSoftKeyboard());

        // Click the sign-up button
        onView(withId(R.id.btnSignUp))
                .perform(click());

        // Verify navigation to MainActivity by checking for btnProfile
        onView(withId(R.id.btnProfile))
                .check(matches(isDisplayed()));
    }
} 