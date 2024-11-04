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
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String authorNotes = etAuthorNotes.getText().toString().trim();
        String llmKind = actvLlmKind.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty() || content.isEmpty() || llmKind.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new post
        String postId = UUID.randomUUID().toString();
        Post post = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            post = new Post(
                postId,          // postID
                title,          // title
                content,        // content
                LocalDateTime.now().toString(),  // timestamp
                "user123",      // createdBy (TODO: Replace with actual user ID)
                llmKind         // LLMKind
            );
        }

        // Set author notes separately if present
        if (!authorNotes.isEmpty()) {
            post.setAuthorNotes(authorNotes);
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
