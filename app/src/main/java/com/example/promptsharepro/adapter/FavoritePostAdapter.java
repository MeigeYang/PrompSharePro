package com.example.promptsharepro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.promptsharepro.R;
import com.example.promptsharepro.model.Favorite;
import com.example.promptsharepro.model.Post;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FavoritePostAdapter extends RecyclerView.Adapter<FavoritePostAdapter.FavoritePostViewHolder> {
    private List<Post> favoritePosts;
    private String currentUserId;
    private DatabaseReference mDatabase;
    private Context context;
    private OnFavoriteChangeListener favoriteChangeListener;

    /**
     * Interface to handle favorite removal callbacks.
     */
    public interface OnFavoriteChangeListener {
        void onFavoriteRemoved(String postID);
    }

    public FavoritePostAdapter(List<Post> favoritePosts, String currentUserId, Context context, OnFavoriteChangeListener listener) {
        this.favoritePosts = favoritePosts;
        this.currentUserId = currentUserId;
        this.context = context;
        this.favoriteChangeListener = listener;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public FavoritePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_post, parent, false);
        return new FavoritePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritePostViewHolder holder, int position) {
        Post post = favoritePosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return favoritePosts.size();
    }

    /**
     * Updates the list of favorite posts and notifies the adapter.
     * @param posts New list of favorite posts.
     */
    public void setPosts(List<Post> posts) {
        this.favoritePosts = posts;
        notifyDataSetChanged();
    }

    class FavoritePostViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView tvPostTitle, tvLlmKind, tvPostContent, tvPostAuthor, tvPostTimestamp;
        ImageButton btnFavorite;

        public FavoritePostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            tvLlmKind = itemView.findViewById(R.id.tvLlmKind);
            tvPostContent = itemView.findViewById(R.id.tvPostContent);
            tvPostAuthor = itemView.findViewById(R.id.tvPostAuthor);
            tvPostTimestamp = itemView.findViewById(R.id.tvPostTimestamp);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }

        public void bind(Post post) {
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

            tvPostAuthor.setText("Posted by: " + post.getCreatedBy());
            tvPostTimestamp.setText(post.getTimestamp());

            // Set favorite icon as filled since it's already a favorite
            btnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_filled));

            // Disable the favorite button to prevent unintentional clicks
            btnFavorite.setEnabled(false);
        }
    }
}
