// app/src/main/java/com/example/promptsharepro/FavoriteActivity.java
package com.example.promptsharepro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.promptsharepro.adapter.FavoritePostAdapter;
import com.example.promptsharepro.model.Favorite;
import com.example.promptsharepro.model.Post;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteActivity extends AppCompatActivity implements FavoritePostAdapter.OnFavoriteChangeListener {
    private static final String TAG = "FavoriteActivity";
    private RecyclerView rvFavoritePosts;
    private FavoritePostAdapter favoritePostAdapter;
    private DatabaseReference mDatabase;
    private String userID;
    private Favorite currentFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Initialize UI components
        initializeViews();

        // Get userID from intent
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        if (userID == null) {
            Log.e(TAG, "No userID provided in intent");
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase and load posts
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "Firebase initialized");

        // Load favorite posts
        loadFavoritePosts();
    }

    /**
     * Initializes the views and sets up the toolbar and RecyclerView.
     */
    private void initializeViews() {
        // Set up toolbar with back navigation
        MaterialToolbar toolbar = findViewById(R.id.favoriteAppBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // Ensure the navigation icon is visible
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                Log.d(TAG, "Toolbar set as support action bar with navigation enabled.");
            }
            toolbar.setNavigationOnClickListener(v -> {
                Log.d(TAG, "Navigation (back) icon clicked.");
                finish();
            });
        } else {
            Log.e(TAG, "Toolbar not found in layout");
        }

        // Initialize RecyclerView
        rvFavoritePosts = findViewById(R.id.rvFavoritePosts);
        if (rvFavoritePosts != null) {
            rvFavoritePosts.setLayoutManager(new LinearLayoutManager(this));
            favoritePostAdapter = new FavoritePostAdapter(new ArrayList<>(), userID, this, this);
            rvFavoritePosts.setAdapter(favoritePostAdapter);
            Log.d(TAG, "RecyclerView initialized and adapter set.");
        } else {
            Log.e(TAG, "RecyclerView not found in layout");
        }
    }

    /**
     * Loads the favorite posts from Firebase.
     */
    private void loadFavoritePosts() {
        if (userID == null) return;

        mDatabase.child("favorites").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot favoriteSnapshot) {
                if (favoriteSnapshot.exists()) {
                    currentFavorite = favoriteSnapshot.getValue(Favorite.class);
                    if (currentFavorite != null && currentFavorite.getFavoritePostIDs() != null && !currentFavorite.getFavoritePostIDs().isEmpty()) {
                        // Extract post IDs from the map keys
                        List<String> favoritePostIDs = new ArrayList<>(currentFavorite.getFavoritePostIDs().keySet());
                        fetchFavoritedPosts(favoritePostIDs);
                    } else {
                        // Handle empty or null favorites
                        favoritePostAdapter.setPosts(new ArrayList<>());
                        Toast.makeText(FavoriteActivity.this, 
                            "No favorites yet", 
                            Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No favorites found for user.");
                    }
                } else {
                    // Handle non-existent favorites node
                    favoritePostAdapter.setPosts(new ArrayList<>());
                    Toast.makeText(FavoriteActivity.this, 
                        "No favorites found", 
                        Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Favorites node does not exist for user.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error loading favorites: " + databaseError.getMessage());
                Toast.makeText(FavoriteActivity.this, 
                    "Error loading favorites: " + databaseError.getMessage(), 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Fetches the actual Post objects based on the list of favorite post IDs.
     */
    private void fetchFavoritedPosts(List<String> favoritePostIDs) {
        if (favoritePostIDs == null || favoritePostIDs.isEmpty()) {
            favoritePostAdapter.setPosts(new ArrayList<>());
            Log.d(TAG, "Favorite post IDs list is empty.");
            return;
        }

        mDatabase.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot postSnapshot) {
                List<Post> favoritePosts = new ArrayList<>();
                for (DataSnapshot snapshot : postSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null && favoritePostIDs.contains(post.getPostID())) {
                        favoritePosts.add(post);
                    }
                }
                favoritePostAdapter.setPosts(favoritePosts);
                Log.d(TAG, "Fetched " + favoritePosts.size() + " favorite posts.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error loading posts: " + databaseError.getMessage());
                Toast.makeText(FavoriteActivity.this, 
                    "Error loading posts: " + databaseError.getMessage(), 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Callback method when a favorite is removed from the favorites list.
     * @param postID The ID of the post that was removed.
     */
    @Override
    public void onFavoriteRemoved(String postID) {
        if (currentFavorite != null && currentFavorite.getFavoritePostIDs() != null) {
            currentFavorite.removeFavoritePostID(postID);
            mDatabase.child("favorites").child(userID).setValue(currentFavorite)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(FavoriteActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Post " + postID + " removed from favorites.");
                        // Refresh the favorite posts list
                        loadFavoritePosts();
                    } else {
                        Toast.makeText(FavoriteActivity.this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to remove post " + postID + " from favorites.");
                    }
                });
        } else {
            Toast.makeText(this, "No favorites to remove from", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Attempted to remove a favorite when none exist.");
        }
    }
}