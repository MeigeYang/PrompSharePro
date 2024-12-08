package com.example.promptsharepro.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.promptsharepro.R;
import com.example.promptsharepro.UpdatePostActivity;
import com.example.promptsharepro.model.Comment;
import com.example.promptsharepro.model.Favorite;
import com.example.promptsharepro.model.Post;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase; 
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts = new ArrayList<>();
    private String currentUserId;
    private DatabaseReference mDatabase;
    private Set<String> favoritePostIDs = new HashSet<>();
    private Context context;

    public PostAdapter(String currentUserId, Context context) {
        this.currentUserId = currentUserId;
        this.context = context;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        loadUserFavorites();
    }

    /**
     * Loads the current user's favorite post IDs from Firebase.
     */
    private void loadUserFavorites() {
        if (currentUserId == null) return;

        mDatabase.child("favorites").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                favoritePostIDs.clear();
                if (snapshot.exists()) {
                    Favorite favorite = snapshot.getValue(Favorite.class);
                    if (favorite != null && favorite.getFavoritePostIDs() != null) {
                        favoritePostIDs.addAll(favorite.getFavoritePostIDs().keySet());
                    }
                }
                notifyDataSetChanged(); // Update the adapter to reflect favorite status
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Failed to load favorites.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the adapter with a new list of favorite post IDs.
     * Call this method whenever the favorite list changes.
     */
    public void updateFavoriteList(Set<String> newFavoritePostIDs) {
        favoritePostIDs.clear();
        favoritePostIDs.addAll(newFavoritePostIDs);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    /**
     * Sets the list of posts and refreshes the adapter.
     *
     * @param posts List of Post objects.
     */
    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for individual posts.
     */
    class PostViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView tvPostTitle, tvLlmKind, tvPostContent, tvAuthorNotes, 
                            tvPostAuthor, tvPostTimestamp, tvNoComments;
        private final MaterialButton btnUpdatePost, btnDeletePost, btnPostComment;
        private final TextInputEditText etNewComment;
        private final RatingBar rbNewCommentRating;
        private final RecyclerView rvComments;
        private final CommentAdapter commentAdapter;
        private final ImageButton btnFavorite;
        private Post post;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // Initialize all views
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            tvLlmKind = itemView.findViewById(R.id.tvLlmKind);
            tvPostContent = itemView.findViewById(R.id.tvPostContent);
            tvAuthorNotes = itemView.findViewById(R.id.tvAuthorNotes);
            tvPostAuthor = itemView.findViewById(R.id.tvPostAuthor);
            tvPostTimestamp = itemView.findViewById(R.id.tvPostTimestamp);
            tvNoComments = itemView.findViewById(R.id.tvNoComments);
            
            btnUpdatePost = itemView.findViewById(R.id.btnUpdatePost);
            btnDeletePost = itemView.findViewById(R.id.btnDeletePost);
            btnPostComment = itemView.findViewById(R.id.btnPostComment);
            
            etNewComment = itemView.findViewById(R.id.etNewComment);
            rbNewCommentRating = itemView.findViewById(R.id.rbNewCommentRating);
            rvComments = itemView.findViewById(R.id.rvComments);
            btnFavorite = itemView.findViewById(R.id.btnFavorite); // Favorite button
            
            // Initialize RecyclerView for comments
            rvComments.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            commentAdapter = new CommentAdapter(currentUserId);
            rvComments.setAdapter(commentAdapter);
        }

        /**
         * Binds the post data to the views.
         *
         * @param post The Post object to bind.
         */
        void bind(Post post) {
            this.post = post;
            tvPostTitle.setText(post.getTitle());
            
            // Handle LLM Kind display with null check
            String llmKind = post.getLlmKind();
            if (llmKind != null && !llmKind.isEmpty()) {
                tvLlmKind.setText("LLM: " + llmKind);
                tvLlmKind.setVisibility(View.VISIBLE);
            } else {
                tvLlmKind.setText("LLM: Unknown");
                tvLlmKind.setVisibility(View.VISIBLE);
            }

            tvPostContent.setText(post.getContent());
            
            // Handle author notes with null check
            String authorNotes = post.getAuthorNotes();
            if (authorNotes != null && !authorNotes.isEmpty()) {
                tvAuthorNotes.setText("Notes: " + authorNotes);
                tvAuthorNotes.setVisibility(View.VISIBLE);
            } else {
                tvAuthorNotes.setVisibility(View.GONE);
            }

            tvPostAuthor.setText("Posted by: " + post.getCreatedBy());
            tvPostTimestamp.setText(post.getTimestamp());

            // Check if the current user is the author of the post
            boolean isAuthor = currentUserId != null && 
                             currentUserId.equals(post.getCreatedBy());
            
            // Show/hide update and delete buttons based on ownership
            btnUpdatePost.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            btnDeletePost.setVisibility(isAuthor ? View.VISIBLE : View.GONE);

            // Only set click listeners if the user is the author
            if (isAuthor) {
                btnUpdatePost.setOnClickListener(v -> {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, UpdatePostActivity.class);
                    intent.putExtra("postId", post.getPostID());
                    intent.putExtra("currentUserId", currentUserId);
                    context.startActivity(intent);
                });

                btnDeletePost.setOnClickListener(v -> {
                    // Show confirmation dialog before deleting
                    android.app.AlertDialog.Builder builder = 
                        new android.app.AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Delete Post")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // Delete the post
                            mDatabase.child("posts").child(post.getPostID())
                                .removeValue()
                                .addOnSuccessListener(aVoid -> 
                                    Toast.makeText(itemView.getContext(),
                                        "Post deleted successfully",
                                        Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> 
                                    Toast.makeText(itemView.getContext(),
                                        "Failed to delete post: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                });
            } else {
                // Hide buttons if not the author
                btnUpdatePost.setVisibility(View.GONE);
                btnDeletePost.setVisibility(View.GONE);
            }

            // Handle Favorite Button
            handleFavoriteButton(post);

            setupCommentSection(post);
        }

        /**
         * Sets up the favorite button's state and click listener.
         *
         * @param post The Post object.
         */
        private void handleFavoriteButton(Post post) {
            // Set initial favorite icon state
            if (favoritePostIDs.contains(post.getPostID())) {
                btnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_filled));
                post.setFavorited(true);
            } else {
                btnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline));
                post.setFavorited(false);
            }

            // Set click listener for favorite button
            btnFavorite.setOnClickListener(v -> {
                if (currentUserId == null) {
                    Toast.makeText(context, "You must be logged in to favorite posts.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isNowFavorited = !favoritePostIDs.contains(post.getPostID());
                if (isNowFavorited) {
                    addPostToFavorites(post.getPostID());
                } else {
                    removePostFromFavorites(post.getPostID());
                }
            });
        }

        /**
         * Updates the favorite icon based on favorite status.
         *
         * @param isFavorited True if the post is favorited, false otherwise.
         */
        private void updateFavoriteIcon(boolean isFavorited) {
            if (isFavorited) {
                btnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_filled));
            } else {
                btnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline));
            }

            // Optional: Provide haptic feedback
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnFavorite.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        }

        /**
         * Adds a post ID to the user's favorites in Firebase.
         *
         * @param postID The ID of the post to add.
         */
        private void addPostToFavorites(String postID) {
            mDatabase.child("favorites").child(currentUserId).child("favoritePostIDs").child(postID)
                .setValue(postID)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoritePostIDs.add(postID);
                        btnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_filled));
                        Toast.makeText(context, "Post added to favorites.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to add to favorites.", Toast.LENGTH_SHORT).show();
                        // Revert the favorite status in the UI
                        btnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline));
                    }
                });
        }

        /**
         * Removes a post ID from the user's favorites in Firebase.
         *
         * @param postID The ID of the post to remove.
         */
        private void removePostFromFavorites(String postID) {
            mDatabase.child("favorites").child(currentUserId).child("favoritePostIDs").child(postID)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoritePostIDs.remove(postID);
                        btnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline));
                        Toast.makeText(context, "Post removed from favorites.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to remove from favorites.", Toast.LENGTH_SHORT).show();
                        // Revert the favorite status in the UI
                        btnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_filled));
                    }
                });
        }

        /**
         * Sets up the comment section for each post.
         *
         * @param post The Post object.
         */
        private void setupCommentSection(Post post) {
            btnPostComment.setOnClickListener(v -> {
                String content = etNewComment.getText().toString().trim();
                float rating = rbNewCommentRating.getRating();

                // Validate rating first
                if (rating == 0) {
                    Toast.makeText(context, 
                        "Please provide a rating before commenting", 
                        Toast.LENGTH_SHORT).show();
                    return;
                }

                if (content.isEmpty()) {
                    Toast.makeText(context, 
                        "Please enter a comment", 
                        Toast.LENGTH_SHORT).show();
                    return;
                }

                String commentId = mDatabase.child("comments").push().getKey();
                if (commentId == null) return;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = sdf.format(new Date());

                Comment comment = new Comment(
                        commentId,
                        post.getPostID(),
                        content,
                        currentUserId,
                        formattedDate,
                        rating
                );

                mDatabase.child("comments").child(commentId).setValue(comment)
                    .addOnSuccessListener(aVoid -> {
                        etNewComment.setText("");
                        rbNewCommentRating.setRating(0);
                        Toast.makeText(context, 
                            "Comment posted successfully", 
                            Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(context, 
                            "Failed to post comment", 
                            Toast.LENGTH_SHORT).show()
                    );
            });

            // Add rating change listener
            rbNewCommentRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                if (fromUser && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ratingBar.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
                }
            });

            // Load existing comments
            loadComments(post.getPostID());
        }

        /**
         * Loads comments related to a specific post.
         *
         * @param postId The ID of the post.
         */
        private void loadComments(String postId) {
            mDatabase.child("comments")
                    .orderByChild("postID")
                    .equalTo(postId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<Comment> comments = new ArrayList<>();
                            for (DataSnapshot commentSnap : snapshot.getChildren()) {
                                Comment comment = commentSnap.getValue(Comment.class);
                                if (comment != null) {
                                    comments.add(comment);
                                }
                            }
                            commentAdapter.setComments(comments);
                            tvNoComments.setVisibility(comments.isEmpty() ? View.VISIBLE : View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context,
                                "Error loading comments: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
} 