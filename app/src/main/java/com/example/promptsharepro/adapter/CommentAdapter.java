package com.example.promptsharepro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.promptsharepro.R;
import com.example.promptsharepro.model.Comment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments = new ArrayList<>();
    private String currentUserId;
    private DatabaseReference mDatabase;

    public CommentAdapter(String currentUserId) {
        this.currentUserId = currentUserId;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
        
        // Show/hide update/delete buttons based on ownership
        boolean isOwner = comment.getCreatedBy().equals(currentUserId);
        holder.btnUpdateComment.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        holder.btnDeleteComment.setVisibility(isOwner ? View.VISIBLE : View.GONE);

        holder.btnUpdateComment.setOnClickListener(v -> showUpdateDialog(v.getContext(), comment));
        holder.btnDeleteComment.setOnClickListener(v -> deleteComment(comment));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    private void showUpdateDialog(Context context, Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_comment, null);
        
        EditText etContent = dialogView.findViewById(R.id.etUpdateComment);
        RatingBar rbRating = dialogView.findViewById(R.id.rbUpdateRating);
        
        etContent.setText(comment.getContent());
        rbRating.setRating(comment.getRating());

        builder.setView(dialogView)
                .setTitle("Update Comment")
                .setPositiveButton("Update", (dialog, which) -> {
                    String newContent = etContent.getText().toString();
                    float newRating = rbRating.getRating();
                    updateComment(comment, newContent, newRating);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateComment(Comment comment, String newContent, float newRating) {
        comment.setContent(newContent);
        comment.setRating(newRating);
        mDatabase.child("comments").child(comment.getCommentID()).setValue(comment);
    }

    private void deleteComment(Comment comment) {
        mDatabase.child("comments").child(comment.getCommentID()).removeValue();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommentAuthor, tvCommentContent, tvCommentTimestamp;
        RatingBar rbCommentRating;
        MaterialButton btnUpdateComment, btnDeleteComment;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommentAuthor = itemView.findViewById(R.id.tvCommentAuthor);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            tvCommentTimestamp = itemView.findViewById(R.id.tvCommentTimestamp);
            rbCommentRating = itemView.findViewById(R.id.rbCommentRating);
            btnUpdateComment = itemView.findViewById(R.id.btnUpdateComment);
            btnDeleteComment = itemView.findViewById(R.id.btnDeleteComment);
        }

        void bind(Comment comment) {
            tvCommentAuthor.setText(comment.getCreatedBy());
            tvCommentContent.setText(comment.getContent());
            tvCommentTimestamp.setText(comment.getTimestamp());
            rbCommentRating.setRating(comment.getRating());
        }
    }
} 