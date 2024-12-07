package com.example.promptsharepro;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.promptsharepro.adapter.SubscriptionAdapter;
import com.example.promptsharepro.model.Comment;
import com.example.promptsharepro.model.Post;
import com.example.promptsharepro.model.Subscription;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.AbstractMap;
import java.util.concurrent.atomic.AtomicInteger;
import androidx.annotation.NonNull; 
import android.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.widget.Toast;

public class RankingSubscriptionsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private LinearLayout llmContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_subscriptions);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize the container for displaying LLM kind information
        llmContainer = findViewById(R.id.llmContainer);

        // Initialize the back button
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Calculate and display real ratings from Firebase
        calculateAndDisplayRealRatings();
    }

    private void calculateAndDisplayRealRatings() {
        Map<String, Float> totalRatings = new HashMap<>();
        Map<String, Integer> ratingCounts = new HashMap<>();

        // Initialize maps for all LLM types with 0 values
        String[] llmTypes = {"GPT-3.5", "GPT-4", "Claude", "Llama", "Other"};
        for (String llmType : llmTypes) {
            totalRatings.put(llmType, 0.0f);
            ratingCounts.put(llmType, 0);
        }

        // Get reference to comments in Firebase
        mDatabase.child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot commentsSnapshot) {
                Log.d("RankingActivity", "Total comments found: " + commentsSnapshot.getChildrenCount());
                int totalComments = (int) commentsSnapshot.getChildrenCount();
                AtomicInteger processedComments = new AtomicInteger(0);

                if (totalComments == 0) {
                    // If no comments, display empty ratings
                    displayCalculatedRatings(totalRatings, ratingCounts);
                    return;
                }

                for (DataSnapshot commentSnapshot : commentsSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        String postID = comment.getPostID();
                        Log.d("RankingActivity", "Processing comment for post: " + postID);

                        // Get the post associated with this comment
                        mDatabase.child("posts").child(postID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                Post post = postSnapshot.getValue(Post.class);
                                if (post != null) {
                                    String llmKind = post.getLlmKind();
                                    float rating = comment.getRating();
                                    Log.d("RankingActivity", "Found post with LLM: " + llmKind + ", rating: " + rating);

                                    // Update totals for this LLM kind
                                    synchronized (totalRatings) {
                                        float currentTotal = totalRatings.getOrDefault(llmKind, 0.0f);
                                        int currentCount = ratingCounts.getOrDefault(llmKind, 0);
                                        totalRatings.put(llmKind, currentTotal + rating);
                                        ratingCounts.put(llmKind, currentCount + 1);
                                    }
                                } else {
                                    Log.w("RankingActivity", "Post not found for ID: " + postID);
                                }

                                // Check if all comments have been processed
                                if (processedComments.incrementAndGet() == totalComments) {
                                    Log.d("RankingActivity", "All comments processed, displaying results");
                                    displayCalculatedRatings(totalRatings, ratingCounts);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("RankingActivity", "Error fetching post: " + error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RankingActivity", "Error fetching comments: " + error.getMessage());
            }
        });
    }

    private void displayCalculatedRatings(Map<String, Float> totalRatings, Map<String, Integer> ratingCounts) {
        // Create a list of LLM ratings for sorting
        List<Map.Entry<String, Float>> averageRatings = new ArrayList<>();
        
        // Calculate average for each LLM type
        for (String llmType : totalRatings.keySet()) {
            // Skip "Unknown" LLM type
            if ("Unknown".equals(llmType)) {
                continue;
            }
            
            float total = totalRatings.get(llmType);
            int count = ratingCounts.get(llmType);
            float average = count > 0 ? total / count : 0.0f;
            
            // Store the average rating
            averageRatings.add(new AbstractMap.SimpleEntry<>(llmType, average));
        }

        // Sort by rating (highest to lowest)
        averageRatings.sort((e1, e2) -> Float.compare(e2.getValue(), e1.getValue()));

        // Clear existing views
        llmContainer.removeAllViews();

        // Display each LLM rating
        for (Map.Entry<String, Float> entry : averageRatings) {
            String llmName = entry.getKey();
            float rating = entry.getValue();
            int count = ratingCounts.get(llmName);

            // Inflate the item layout
            View llmView = LayoutInflater.from(this).inflate(R.layout.item_llm_rating, llmContainer, false);

            // Set the LLM name and rating
            TextView tvLlmName = llmView.findViewById(R.id.tvLlmName);
            TextView tvRating = llmView.findViewById(R.id.tvRating);
            TextView tvCount = llmView.findViewById(R.id.tvCount);
            RatingBar rbAverageRating = llmView.findViewById(R.id.rbAverageRating);

            tvLlmName.setText(llmName);
            tvRating.setText(String.format("Average Rating: %.2f / 5", rating));
            tvCount.setText(String.format("Based on %d ratings", count));
            rbAverageRating.setRating(rating);

            // Add the view to the container
            llmContainer.addView(llmView);

            // Set up subscription views
            RecyclerView rvSubscriptions = llmView.findViewById(R.id.rvSubscriptions);
            Button btnSubscribe = llmView.findViewById(R.id.btnSubscribe);

            // Load existing subscriptions
            loadSubscriptions(llmName, rvSubscriptions);

            // Set up subscribe button
            btnSubscribe.setOnClickListener(v -> showSubscriptionDialog(llmName, rvSubscriptions));
        }
    }

    private void showSubscriptionDialog(String llmType, RecyclerView rvSubscriptions) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_subscription, null);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);

        new AlertDialog.Builder(this)
                .setTitle("Join Subscription Sharing")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Generate subscription ID
                    String subscriptionId = mDatabase.child("subscriptions").push().getKey();
                    if (subscriptionId == null) return;

                    // Create timestamp
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String timestamp = sdf.format(new Date());

                    // Create subscription object
                    Subscription subscription = new Subscription(subscriptionId, llmType, name, email, timestamp);

                    // Save to Firebase
                    mDatabase.child("subscriptions").child(subscriptionId).setValue(subscription)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Subscription added successfully", Toast.LENGTH_SHORT).show();
                                loadSubscriptions(llmType, rvSubscriptions);
                            })
                            .addOnFailureListener(e -> 
                                Toast.makeText(this, "Failed to add subscription", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadSubscriptions(String llmType, RecyclerView recyclerView) {
        mDatabase.child("subscriptions")
                .orderByChild("llmType")
                .equalTo(llmType)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Subscription> subscriptions = new ArrayList<>();
                        for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                            Subscription subscription = subSnapshot.getValue(Subscription.class);
                            if (subscription != null) {
                                subscriptions.add(subscription);
                            }
                        }
                        // Update RecyclerView
                        updateSubscriptionsList(recyclerView, subscriptions);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("RankingActivity", "Error loading subscriptions", error.toException());
                    }
                });
    }

    private void updateSubscriptionsList(RecyclerView recyclerView, List<Subscription> subscriptions) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        // Create and set adapter (you'll need to create this adapter class)
        SubscriptionAdapter adapter = new SubscriptionAdapter(subscriptions);
        recyclerView.setAdapter(adapter);
    }
}
