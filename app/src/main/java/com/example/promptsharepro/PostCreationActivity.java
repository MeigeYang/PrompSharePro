package com.example.promptsharepro;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.promptsharepro.model.Post;
import java.time.LocalDateTime;
import java.util.UUID;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

public class PostCreationActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etContent, etAuthorNotes;
    private AutoCompleteTextView actvLlmKind;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        etAuthorNotes = findViewById(R.id.etAuthorNotes);
        actvLlmKind = findViewById(R.id.actvLlmKind);
        Button btnSubmitPost = findViewById(R.id.btnSubmitPost);

        // Setup LLM dropdown
        String[] llmOptions = {"GPT-3.5", "GPT-4", "Claude", "Llama", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, llmOptions);
        actvLlmKind.setAdapter(adapter);

        // Setup submit button click listener
        btnSubmitPost.setOnClickListener(v -> savePost());
    }

    private void savePost() {
        try {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            String authorNotes = etAuthorNotes.getText().toString().trim();
            String llmKind = actvLlmKind.getText().toString().trim();

            // Validate inputs
            if (title.isEmpty() || content.isEmpty() || llmKind.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String postId = UUID.randomUUID().toString();
            Log.d("PostCreation", "Creating post object with ID: " + postId);
            
            Post post = new Post(
                postId,
                title,
                content,
                LocalDateTime.now().toString(),
                "user123",
                llmKind
            );
            
            if (!authorNotes.isEmpty()) {
                post.setAuthorNotes(authorNotes);
            }
        // Create new post
        String postId = UUID.randomUUID().toString();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Post post = new Post(
                postId,       // postID
                title,        // title
                content,      // content
                timestamp,    // timestamp
                "user123",    // createdBy (TODO: Replace with actual user ID)
                llmKind       // LLMKind
        );

        // Set author notes separately if present
        if (!authorNotes.isEmpty()) {
            post.setAuthorNotes(authorNotes);
        }

            // Add detailed logging
            Log.d("PostCreation", "Post object created: " + post.toString());
            Log.d("PostCreation", "Attempting to save post to Firebase...");
            
            DatabaseReference postRef = mDatabase.child("posts").child(postId);
            
            postRef.setValue(post)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PostCreation", "Post saved successfully: " + postId);
                    Toast.makeText(PostCreationActivity.this, 
                        "Post created successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Add delay before finishing activity
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        finish();
                    }, 1000); // 1 second delay
                })
                .addOnFailureListener(e -> {
                    Log.e("PostCreation", "Error saving post: " + postId, e);
                    Toast.makeText(PostCreationActivity.this, 
                        "Error creating post: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
            
        } catch (Exception e) {
            Log.e("PostCreation", "Unexpected error while saving post", e);
            Toast.makeText(this, "Unexpected error: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
        // Save to Firebase
        mDatabase.child("posts").child(postId).setValue(post)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PostCreationActivity.this,
                            "Post created successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to previous screen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PostCreationActivity.this,
                            "Error creating post: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

}
