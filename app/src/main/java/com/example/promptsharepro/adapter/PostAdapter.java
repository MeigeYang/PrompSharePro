package com.example.promptsharepro.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.promptsharepro.R;
import com.example.promptsharepro.model.Post;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts = new ArrayList<>();
    private DatabaseReference mDatabase;

    public PostAdapter() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        TextView tvPostTitle, tvLlmKind, tvPostContent, tvAuthorNotes, 
                tvPostAuthor, tvPostTimestamp, tvNoComments;
        MaterialButton btnUpdatePost, btnDeletePost, btnAddComment;

        PostViewHolder(View itemView) {
            super(itemView);
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            tvLlmKind = itemView.findViewById(R.id.tvLlmKind);
            tvPostContent = itemView.findViewById(R.id.tvPostContent);
            tvAuthorNotes = itemView.findViewById(R.id.tvAuthorNotes);
            tvPostAuthor = itemView.findViewById(R.id.tvPostAuthor);
            tvPostTimestamp = itemView.findViewById(R.id.tvPostTimestamp);
            tvNoComments = itemView.findViewById(R.id.tvNoComments);
            btnUpdatePost = itemView.findViewById(R.id.btnUpdatePost);
            btnDeletePost = itemView.findViewById(R.id.btnDeletePost);
            btnAddComment = itemView.findViewById(R.id.btnPostComment);


        }

        void bind(final Post post) {
            tvPostTitle.setText(post.getTitle());
            tvLlmKind.setText("LLM Kind: " + post.getLLMKind());
            tvPostContent.setText(post.getContent());
            
            if (post.getAuthorNotes() != null && !post.getAuthorNotes().isEmpty()) {
                tvAuthorNotes.setVisibility(View.VISIBLE);
                tvAuthorNotes.setText("Author Notes: " + post.getAuthorNotes());
            } else {
                tvAuthorNotes.setVisibility(View.GONE);
            }
            
            tvPostAuthor.setText("Posted by: " + post.getCreatedBy());
            tvPostTimestamp.setText("Posted on: " + post.getTimestamp());

            // Handle delete button click
            btnDeletePost.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        mDatabase.child("posts").child(post.getPostID()).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(v.getContext(), 
                                    "Post deleted successfully", 
                                    Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(v.getContext(), 
                                    "Error deleting post: " + e.getMessage(), 
                                    Toast.LENGTH_LONG).show();
                            });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            });

            // Show "No comments" if there are no comments
            if (post.getComments() == null || post.getComments().isEmpty()) {
                tvNoComments.setVisibility(View.VISIBLE);
            } else {
                tvNoComments.setVisibility(View.GONE);
            }
        }
    }
} 