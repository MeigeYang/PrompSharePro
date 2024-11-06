package com.example.promptsharepro.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.promptsharepro.R;
import com.example.promptsharepro.model.Comment;
import com.example.promptsharepro.model.Post;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.promptsharepro.UpdatePostActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts = new ArrayList<>();
    private String currentUserId;
    private DatabaseReference mDatabase;

    public PostAdapter(String currentUserId) {
        this.currentUserId = currentUserId;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
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

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPostTitle, tvLlmKind, tvPostContent, tvAuthorNotes, 
                        tvPostAuthor, tvPostTimestamp, tvNoComments;
        private MaterialButton btnUpdatePost, btnDeletePost, btnPostComment;
        private TextInputEditText etNewComment;
        private RatingBar rbNewCommentRating;
        private RecyclerView rvComments;
        private CommentAdapter commentAdapter;

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
            
            // Initialize RecyclerView for comments
            rvComments.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            commentAdapter = new CommentAdapter(currentUserId);
            rvComments.setAdapter(commentAdapter);
        }

        void bind(Post post) {
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
            }

            setupCommentSection(post);
        }

        private void setupCommentSection(Post post) {
            btnPostComment.setOnClickListener(v -> {
                String content = etNewComment.getText().toString().trim();
                float rating = rbNewCommentRating.getRating();

                // Validate rating first
                if (rating == 0) {
                    Toast.makeText(itemView.getContext(), 
                        "Please provide a rating before commenting", 
                        Toast.LENGTH_SHORT).show();
                    return;
                }

                // Then validate content
                if (content.isEmpty()) {
                    Toast.makeText(itemView.getContext(), 
                        "Please write a comment", 
                        Toast.LENGTH_SHORT).show();
                    return;
                }

                String commentId = mDatabase.child("comments").push().getKey();
                if (commentId == null) return;

                Comment comment = new Comment(
                    commentId,
                    post.getPostID(),
                    content,
                    currentUserId,
                    LocalDateTime.now().toString(),
                    rating
                );

                mDatabase.child("comments").child(commentId).setValue(comment)
                    .addOnSuccessListener(aVoid -> {
                        etNewComment.setText("");
                        rbNewCommentRating.setRating(0);
                        Toast.makeText(itemView.getContext(), 
                            "Comment posted successfully", 
                            Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(itemView.getContext(), 
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
                            Toast.makeText(itemView.getContext(),
                                "Error loading comments: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
} 