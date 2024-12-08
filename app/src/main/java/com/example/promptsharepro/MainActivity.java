package com.example.promptsharepro;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.promptsharepro.adapter.PostAdapter;
import com.example.promptsharepro.model.Post;
import com.example.promptsharepro.model.User;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.*;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

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
    User currUser = null;
    private MaterialButton btnProfile;
    private MaterialButtonToggleGroup searchToggleGroup;
    private MaterialButton btnRankingSubscriptions;
    private MaterialButton btnFavorites;
    private enum SearchMode {
        CONTENT,
        TITLE,
        LLM
    }
    private SearchMode currentSearchMode = SearchMode.CONTENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String username = extras.getString("username");
            String email = extras.getString("email");
            String id = extras.getString("ID");
            String password = extras.getString("password");
            //make current user
            currUser = new User(username, email, id, password);
        }

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize RecyclerView
        rvPosts = findViewById(R.id.rvPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(currUser.getUscID(), this);
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
            intent.putExtra("ID", currUser.getUscID());
            startActivity(intent);
        });

        // Initialize and set up profile button
        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfile();
            }
        });

        // Initialize and set up the Ranking/Subscriptions button
        btnRankingSubscriptions = findViewById(R.id.btnRankingSubscriptions);
        btnRankingSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRankingSubscriptions();
            }
        });

        // Initialize and set up the Favorites button
        btnFavorites = findViewById(R.id.btnFavorites);
        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFavorites();
            }
        });

        // Window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topAppBar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize toggle group
        searchToggleGroup = findViewById(R.id.searchToggleGroup);
        setupSearchToggle();
        
        // Select default button
        searchToggleGroup.check(R.id.btnSearchContent);
    }

    private void navigateToProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        if (currUser != null) {
            intent.putExtra("username", currUser.getUsername());
            intent.putExtra("email", currUser.getEmail());
            intent.putExtra("ID", currUser.getUscID());
            intent.putExtra("password", currUser.getPassword());
        }
        startActivity(intent);
    }

    private void navigateToRankingSubscriptions() {
        Intent intent = new Intent(MainActivity.this, RankingSubscriptionsActivity.class);
        startActivity(intent);
    }

    private void navigateToFavorites() {
        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
        if (currUser != null) {
            intent.putExtra("userID", currUser.getUscID());
        }
        startActivity(intent);
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

    private void setupSearchToggle() {
        searchToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnSearchContent) {
                    currentSearchMode = SearchMode.CONTENT;
                } else if (checkedId == R.id.btnSearchTitle) {
                    currentSearchMode = SearchMode.TITLE;
                } else if (checkedId == R.id.btnSearchLLM) {
                    currentSearchMode = SearchMode.LLM;
                }
                
                // Perform search with current query and new mode
                String currentQuery = etSearch.getText().toString();
                if (!currentQuery.isEmpty()) {
                    performSearch(currentQuery);
                }
            }
        });
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            postAdapter.setPosts(allPosts);
            return;
        }

        List<Post> searchResults;
        switch (currentSearchMode) {
            case TITLE:
                searchResults = searchHandler.searchByTitle(query);
                break;
            case LLM:
                searchResults = searchHandler.searchByLLMKind(query);
                break;
            case CONTENT:
            default:
                searchResults = searchHandler.fullTextSearch(query);
                break;
        }
        
        postAdapter.setPosts(searchResults);
        
        // Show toast to indicate search mode
        String modeMessage = "Searching by " + currentSearchMode.toString().toLowerCase();
        Toast.makeText(this, modeMessage, Toast.LENGTH_SHORT).show();
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
}