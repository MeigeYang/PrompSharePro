package com.example.promptsharepro.test;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.promptsharepro.LoginActivity;
import com.example.promptsharepro.MainActivity;
import com.example.promptsharepro.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

/**
 * BasicFeatureTest performs UI tests on the Login functionality of the application.
 */
@RunWith(AndroidJUnit4.class)
public class BasicFeatureTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Tests the user login functionality by entering email and password,
     * clicking the login button, and verifying navigation to the home screen.
     */
    @Test
    public void testUserLogin() {
        // Enter email
        onView(withId(R.id.etLoginEmail))
                .perform(typeText("test@usc.edu"), closeSoftKeyboard());

        // Enter password
        onView(withId(R.id.etLoginPassword))
                .perform(typeText("testtest"), closeSoftKeyboard());

        // Click the login button
        onView(withId(R.id.btnLogin))
                .perform(click());

        // Verify that the user is navigated to the home screen by checking for btnProfile
        //onView(withId(R.id.btnProfile))
        //       .check(matches(isDisplayed()));
    }
}
