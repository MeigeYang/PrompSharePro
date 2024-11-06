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
import android.app.ProgressDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostCreationActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etContent, etAuthorNotes;
    private AutoCompleteTextView actvLlmKind;
    private DatabaseReference mDatabase;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);

        // Get current user ID from intent
        currentUserId = getIntent().getStringExtra("ID");
        if (currentUserId == null) {
            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

        // Generate unique ID for the post
        String postId = UUID.randomUUID().toString();

        // Use SimpleDateFormat for timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(new Date());

        // Create post object with current user ID
        Post post = new Post(
            postId,
            title,
            content,
            timestamp,
            currentUserId,  // Use the actual user ID instead of "user123"
            llmKind
        );
        
        if (!authorNotes.isEmpty()) {
            post.setAuthorNotes(authorNotes);
        }

        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating post...");
        progressDialog.show();

        // Save to Firebase
        mDatabase.child("posts").child(postId).setValue(post)
            .addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                Toast.makeText(PostCreationActivity.this, 
                    "Post created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Log.e("PostCreation", "Error saving post: " + postId, e);
                progressDialog.dismiss();
                Toast.makeText(PostCreationActivity.this, 
                    "Error creating post: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            });
    }

}
