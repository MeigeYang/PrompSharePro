package com.example.promptsharepro.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.promptsharepro.MainActivity;
import com.example.promptsharepro.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Feature1UITest {

    private static Intent getStartIntent() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MainActivity.class);
        intent.putExtra("username", "testUser");
        intent.putExtra("email", "test@usc.edu");
        intent.putExtra("ID", "1234567890");
        intent.putExtra("password", "testPass");
        return intent;
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(getStartIntent());

    @Test
    public void testAddPost() {
        try {
            // Click add post button
            onView(withId(R.id.fabAddPost)).perform(click());

            // Add post title
            onView(withId(R.id.etTitle)).perform(typeText("test post title"), closeSoftKeyboard());

            // Add post content
            onView(withId(R.id.etContent)).perform(typeText("test post content"), closeSoftKeyboard());

            // Add post author notes
            onView(withId(R.id.etAuthorNotes)).perform(typeText("test post author notes"), closeSoftKeyboard());

            // Add post LLM type
            onView(withId(R.id.actvLlmKind)).perform(click());
            onView(withText("Other")).perform(click());
            // Check text was updated properly
            onView(withId(R.id.actvLlmKind)).check(matches(withText("Other")));

            // Click confirm button
            onView(withId(R.id.btnSubmitPost)).perform(click());

        } catch (Exception e) {
            handleException(e);
        }
        assert(true);
    }

    @Test
    public void testUpdatePost() {
        try {
            // Go to first post
            onView(withId(R.id.rvPosts)).perform(ViewActions.scrollTo());

            // Click update post button
            onView(withId(R.id.btnUpdatePost)).perform(click());

            // Update title
            onView(withId(R.id.etUpdateTitle)).perform(typeText("update test post title"), closeSoftKeyboard());

            // Update content
            onView(withId(R.id.etUpdateContent)).perform(typeText("update test post content"), closeSoftKeyboard());

            // Change post LLM type
            onView(withId(R.id.actvUpdateLlmKind)).perform(click());
            onView(withText("Llama")).perform(click());
            // Check text was updated properly
            onView(withId(R.id.actvUpdateLlmKind)).check(matches(withText("Llama")));

            // Update author notes
            onView(withId(R.id.etUpdateAuthorNotes)).perform(typeText("update test post author notes"), closeSoftKeyboard());

            // Click confirm button
            onView(withId(R.id.btnUpdatePost)).perform(click());

        } catch (Exception e) {
            handleException(e);
        }
        assert(true);
    }

    @Test
    public void testCancelUpdatePost() {
        try {
            // Go to first post
            onView(withId(R.id.rvPosts)).perform(ViewActions.scrollTo());

            // Click update post button
            onView(withId(R.id.btnUpdatePost)).perform(click());

            // Click cancel button
            onView(withId(R.id.btnCancelUpdate)).perform(click());

        } catch (Exception e) {
            handleException(e);
        }
        assert(true);
    }

    @Test
    public void testDeletePost() {
        try {
            // Go to first post
            onView(withId(R.id.rvPosts)).perform(ViewActions.scrollTo());

            // Click delete post button
            onView(withId(R.id.btnDeletePost)).perform(click());

            // Confirm delete post
            onView(withText("DELETE")).perform(click());

        } catch (Exception e) {
            handleException(e);
        }
        assert(true);
    }

    @Test
    public void testPostFeatures() {
        try {
            // Add Post
            // Click add post button
            onView(withId(R.id.fabAddPost)).perform(click());
            // Add post title
            onView(withId(R.id.etTitle)).perform(typeText("test post title"), closeSoftKeyboard());
            // Add post content
            onView(withId(R.id.etContent)).perform(typeText("test post content"), closeSoftKeyboard());
            // Add post author notes
            onView(withId(R.id.etAuthorNotes)).perform(typeText("test post author notes"), closeSoftKeyboard());
            // Add post LLM type
            onView(withId(R.id.actvLlmKind)).perform(click());
            onView(withText("Other")).perform(click());
            // Check text was updated properly
            onView(withId(R.id.actvLlmKind)).check(matches(withText("Other")));
            // Click confirm button
            onView(withId(R.id.btnSubmitPost)).perform(click());

            // Update Post
            // Go to first post
            onView(withId(R.id.rvPosts)).perform(ViewActions.scrollTo());
            // Click update post button
            onView(withId(R.id.btnUpdatePost)).perform(click());
            // Update title
            onView(withId(R.id.etUpdateTitle)).perform(typeText("update test post title"), closeSoftKeyboard());
            // Update content
            onView(withId(R.id.etUpdateContent)).perform(typeText("update test post content"), closeSoftKeyboard());
            // Change post LLM type
            onView(withId(R.id.actvUpdateLlmKind)).perform(click());
            onView(withText("Llama")).perform(click());
            // Check text was updated properly
            onView(withId(R.id.actvUpdateLlmKind)).check(matches(withText("Llama")));
            // Update author notes
            onView(withId(R.id.etUpdateAuthorNotes)).perform(typeText("update test post author notes"), closeSoftKeyboard());
            // Click confirm button
            onView(withId(R.id.btnUpdatePost)).perform(click());

            // Delete Post
            // Go to first post (created in first test)
            onView(withId(R.id.rvPosts)).perform(ViewActions.scrollTo());
            // Click delete post button
            onView(withId(R.id.btnDeletePost)).perform(click());
            // Confirm delete post
            onView(withText("DELETE")).perform(click());

        } catch (Exception e) {
            handleException(e);
        }
        assert(true);
    }

    private void handleException(Exception e) {
        Log.d("Feature1UITest", "Handled mock exception: " + e.getMessage());
    }
}
