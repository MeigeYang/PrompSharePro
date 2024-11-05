package com.example.promptsharepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.promptsharepro.model.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference database;
    private TextInputEditText emailET;
    private TextInputEditText idET;
    private TextInputEditText usernameET;
    private TextInputEditText passwordET;

    private User currUser;

    private boolean isEmailEditable = false;
    private boolean isIdEditable = false;
    private boolean isUsernameEditable = false;
    private boolean isPasswordEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize toolbar and set navigation click listener
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        // Get current user data from intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String username = extras.getString("username");
            String email = extras.getString("email");
            String id = extras.getString("ID");
            String password = extras.getString("password");
            // Create current user
            currUser = new User(email, username, id, password);
        }

        // Initialize views
        emailET = findViewById(R.id.etEmail);
        idET = findViewById(R.id.etId);
        usernameET = findViewById(R.id.etUsername);
        passwordET = findViewById(R.id.etPassword);

        // Get users database reference
        database = FirebaseDatabase.getInstance().getReference("users");

        // Set text to user information
        emailET.setText(currUser.getEmail());
        idET.setText(currUser.getUscID());
        usernameET.setText(currUser.getUsername());
        passwordET.setText(currUser.getPassword());
    }

    // Edit Email
    public void editEmail(View view) {
        MaterialButton button = (MaterialButton) view;

        if (!isEmailEditable) {
            // Enable editing
            emailET.setEnabled(true);
            emailET.requestFocus();
            button.setText("Save");
            isEmailEditable = true;
        } else {
            // Save changes
            String newEmail = emailET.getText().toString().trim();

            // Validate email
            if (newEmail.isEmpty()) {
                emailET.setError("Email cannot be empty");
                return;
            }

            if (!newEmail.endsWith("@usc.edu")) {
                emailET.setError("Must be a USC email");
                return;
            }

            if (newEmail.equals(currUser.getEmail())) {
                // No changes made
                emailET.setEnabled(false);
                button.setText("Edit");
                isEmailEditable = false;
                return;
            }

            // Check if email is already in use
            checkEmailExists(newEmail, exists -> {
                if (exists) {
                    emailET.setError("Email already in use");
                } else {
                    // Update email in database
                    currUser.setEmail(newEmail);
                    database.child(currUser.getUscID()).setValue(currUser).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                            emailET.setEnabled(false);
                            button.setText("Edit");
                            isEmailEditable = false;
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    // Edit ID
    public void editId(View view) {
        MaterialButton button = (MaterialButton) view;

        if (!isIdEditable) {
            // Enable editing
            idET.setEnabled(true);
            idET.requestFocus();
            button.setText("Save");
            isIdEditable = true;
        } else {
            // Save changes
            String newId = idET.getText().toString().trim();

            // Validate ID
            if (newId.isEmpty()) {
                idET.setError("ID cannot be empty");
                return;
            }

            if (newId.length() != 10 || !newId.matches("\\d+")) {
                idET.setError("Invalid ID: Must be exactly 10 digits and contain only numbers.");
                return;
            }

            if (newId.equals(currUser.getUscID())) {
                // No changes made
                idET.setEnabled(false);
                button.setText("Edit");
                isIdEditable = false;
                return;
            }

            // Check if ID is already in use
            checkIdExists(newId, exists -> {
                if (exists) {
                    idET.setError("ID already in use");
                } else {
                    // Update ID in database
                    String oldId = currUser.getUscID();
                    currUser.setUscID(newId);

                    // Remove old entry
                    database.child(oldId).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Add new entry with new ID
                            database.child(newId).setValue(currUser).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "ID updated successfully", Toast.LENGTH_SHORT).show();
                                    idET.setEnabled(false);
                                    button.setText("Edit");
                                    isIdEditable = false;
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Failed to update ID", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to remove old ID", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    // Edit Username
    public void editUsername(View view) {
        MaterialButton button = (MaterialButton) view;

        if (!isUsernameEditable) {
            // Enable editing
            usernameET.setEnabled(true);
            usernameET.requestFocus();
            button.setText("Save");
            isUsernameEditable = true;
        } else {
            // Save changes
            String newUsername = usernameET.getText().toString().trim();

            // Validate username
            if (newUsername.isEmpty()) {
                usernameET.setError("Username cannot be empty");
                return;
            }

            if (newUsername.equals(currUser.getUsername())) {
                // No changes made
                usernameET.setEnabled(false);
                button.setText("Edit");
                isUsernameEditable = false;
                return;
            }

            // Check if username is already in use
            checkUsernameExists(newUsername, exists -> {
                if (exists) {
                    usernameET.setError("Username already in use");
                } else {
                    // Update username in database
                    currUser.setUsername(newUsername);
                    database.child(currUser.getUscID()).setValue(currUser).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                            usernameET.setEnabled(false);
                            button.setText("Edit");
                            isUsernameEditable = false;
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    // Edit Password
    public void editPassword(View view) {
        MaterialButton button = (MaterialButton) view;

        if (!isPasswordEditable) {
            // Enable editing
            passwordET.setEnabled(true);
            passwordET.requestFocus();
            button.setText("Save");
            isPasswordEditable = true;
        } else {
            // Save changes
            String newPassword = passwordET.getText().toString();

            // Validate password
            if (newPassword.isEmpty()) {
                passwordET.setError("Password cannot be empty");
                return;
            }

            if (newPassword.equals(currUser.getPassword())) {
                // No changes made
                passwordET.setEnabled(false);
                button.setText("Edit");
                isPasswordEditable = false;
                return;
            }

            // Update password in database
            currUser.setPassword(newPassword);
            database.child(currUser.getUscID()).setValue(currUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    passwordET.setEnabled(false);
                    button.setText("Edit");
                    isPasswordEditable = false;
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Sign Out
    public void signOut(View view) {
        // Redirect to login screen
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Check if email exists
    private void checkEmailExists(String email, OnExistsCallback callback) {
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && !user.getUscID().equals(currUser.getUscID())) {
                        exists = true;
                        break;
                    }
                }
                callback.onResult(exists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onResult(false);
            }
        });
    }

    // Check if ID exists
    private void checkIdExists(String id, OnExistsCallback callback) {
        database.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = snapshot.exists() && !id.equals(currUser.getUscID());
                callback.onResult(exists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onResult(false);
            }
        });
    }

    // Check if username exists
    private void checkUsernameExists(String username, OnExistsCallback callback) {
        database.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && !user.getUscID().equals(currUser.getUscID())) {
                        exists = true;
                        break;
                    }
                }
                callback.onResult(exists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onResult(false);
            }
        });
    }

    // Callback interface
    private interface OnExistsCallback {
        void onResult(boolean exists);
    }
}

