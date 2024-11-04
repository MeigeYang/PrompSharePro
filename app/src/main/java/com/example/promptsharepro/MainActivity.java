package com.example.promptsharepro;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.promptsharepro.adapter.PostAdapter;
import com.example.promptsharepro.model.Post;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.*;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;
    private DatabaseReference mDatabase;
    private SearchHandler searchHandler;
    private TextInputEditText etSearch;
    private List<Post> allPosts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize RecyclerView
        rvPosts = findViewById(R.id.rvPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter();
        rvPosts.setAdapter(postAdapter);

        // Initialize SearchHandler
        searchHandler = new SearchHandler(new ArrayList<>());

        // Initialize search
        etSearch = findViewById(R.id.etSearch);
        setupSearch();

        // Load posts
        loadPosts();

        // FAB click listener
        ExtendedFloatingActionButton fabAddPost = findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PostCreationActivity.class);
            startActivity(intent);
        });

        // Window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topAppBar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupSearch() {
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.getText().toString());
                return true;
            }
            return false;
        });

        // Add text change listener for real-time search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                performSearch(s.toString());
            }
        });
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            postAdapter.setPosts(allPosts);
            return;
        }

        // Perform full text search by default
        List<Post> searchResults = searchHandler.fullTextSearch(query);
        postAdapter.setPosts(searchResults);
    }

    private void loadPosts() {
        mDatabase.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allPosts.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        allPosts.add(post);
                    }
                }

                // Sort posts by timestamp (newest first)
                Collections.sort(allPosts, new Comparator<Post>() {
                    @Override
                    public int compare(Post post1, Post post2) {
                        if (post1.getTimestamp() == null || post2.getTimestamp() == null) {
                            return 0;
                        }
                        return post2.getTimestamp().compareTo(post1.getTimestamp());
                    }
                });

                // Update SearchHandler with new posts
                searchHandler.updatePosts(allPosts);
                
                // Update adapter with all posts or filtered posts if search is active
                String currentSearch = etSearch.getText().toString();
                if (currentSearch.isEmpty()) {
                    postAdapter.setPosts(allPosts);
                } else {
                    performSearch(currentSearch);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "Error loading posts", databaseError.toException());
                Toast.makeText(MainActivity.this, 
                    "Error loading posts: " + databaseError.getMessage(), 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    // Optional: Add methods to switch search modes
    private void searchByLLMKind(String query) {
        List<Post> results = searchHandler.searchByLLMKind(query);
        postAdapter.setPosts(results);
    }

    private void searchByTitle(String query) {
        List<Post> results = searchHandler.searchByTitle(query);
        postAdapter.setPosts(results);
    }
}