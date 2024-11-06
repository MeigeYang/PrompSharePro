package com.example.promptsharepro;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.promptsharepro.model.Post;
import androidx.annotation.NonNull;
import android.app.ProgressDialog;

public class UpdatePostActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etContent, etAuthorNotes;
    private AutoCompleteTextView actvLlmKind;
    private MaterialButton btnUpdatePost, btnCancel;
    private DatabaseReference mDatabase;
    private String postId;
    private String currentUserId;
    private Post currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);
        
        // Get the post ID and current user ID from intent
        postId = getIntent().getStringExtra("postId");
        currentUserId = getIntent().getStringExtra("currentUserId");
        
        // Validate both postId and currentUserId
        if (postId == null || currentUserId == null) {
            Toast.makeText(this, "Error: Missing required information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        initializeViews();
        
        // Setup LLM dropdown
        setupLlmDropdown();
        
        // Load current post data
        loadPostData();
        
        // Setup button click listeners
        setupButtons();
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.etUpdateTitle);
        etContent = findViewById(R.id.etUpdateContent);
        etAuthorNotes = findViewById(R.id.etUpdateAuthorNotes);
        actvLlmKind = findViewById(R.id.actvUpdateLlmKind);
        btnUpdatePost = findViewById(R.id.btnUpdatePost);
        btnCancel = findViewById(R.id.btnCancelUpdate);
    }

    private void setupLlmDropdown() {
        String[] llmOptions = {"GPT-3.5", "GPT-4", "Claude", "Llama", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, llmOptions);
        actvLlmKind.setAdapter(adapter);
    }

    private void loadPostData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading post data...");
        progressDialog.show();

        mDatabase.child("posts").child(postId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    currentPost = snapshot.getValue(Post.class);
                    if (currentPost != null) {
                        // Check if the current user is the author
                        if (!currentPost.getCreatedBy().equals(currentUserId)) {
                            Toast.makeText(UpdatePostActivity.this, 
                                "You are not authorized to edit this post", 
                                Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        
                        // Populate fields with current data
                        etTitle.setText(currentPost.getTitle());
                        etContent.setText(currentPost.getContent());
                        etAuthorNotes.setText(currentPost.getAuthorNotes());
                        actvLlmKind.setText(currentPost.getLlmKind(), false);
                    } else {
                        Toast.makeText(UpdatePostActivity.this, 
                            "Error: Post not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdatePostActivity.this,
                        "Error loading post: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
    }

    private void setupButtons() {
        btnUpdatePost.setOnClickListener(v -> updatePost());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void updatePost() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String authorNotes = etAuthorNotes.getText().toString().trim();
        String llmKind = actvLlmKind.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty() || content.isEmpty() || llmKind.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", 
                Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating post...");
        progressDialog.show();

        // Update only the fields that can be changed
        currentPost.setTitle(title);
        currentPost.setContent(content);
        currentPost.setAuthorNotes(authorNotes);
        currentPost.setLlmKind(llmKind);

        // Save to Firebase
        mDatabase.child("posts").child(postId).setValue(currentPost)
            .addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                Toast.makeText(UpdatePostActivity.this,
                    "Post updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(UpdatePostActivity.this,
                    "Error updating post: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            });
    }
} 