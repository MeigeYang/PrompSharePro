package com.example.promptsharepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.promptsharepro.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Handles user login functionality.
 */
public class LoginActivity extends AppCompatActivity {

    private DatabaseReference database;
    private EditText emailET;
    private EditText passwordET;
    private TextView error;
    private User matchingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailET = findViewById(R.id.etLoginEmail);
        passwordET = findViewById(R.id.etLoginPassword);
        error = findViewById(R.id.loginError);
        error.setText("");

        // Initialize Firebase reference
        database = FirebaseDatabase.getInstance().getReference("users");
    }

    /**
     * Initiates the login process when the login button is clicked.
     *
     * @param view The view that was clicked.
     */
    public void logIn(View view) {
        // Get user inputs
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString();

        // Input validation
        if (email.isEmpty() || password.isEmpty()) {
            error.setText("Fill in all fields");
            return;
        }

        // Disable the login button to prevent multiple clicks
        view.setEnabled(false);

        // Check if account exists and validate credentials
        findUser(email, password, view);
    }

    /**
     * Searches for the user in Firebase and validates credentials.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @param view     The login button view to re-enable in case of failure.
     */
    private void findUser(String email, String password, View view) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userFound = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && user.getEmail().equals(email)) {
                        userFound = true;
                        if (user.getPassword().equals(password)) {
                            // Credentials are valid; proceed to MainActivity
                            navigateToMainActivity(user);
                        } else {
                            // Password mismatch
                            error.setText("Password doesn't match");
                            view.setEnabled(true);
                        }
                        return;
                    }
                }
                if (!userFound) {
                    // Email not found
                    error.setText("Email not found");
                    view.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }
        });
    }

    /**
     * Navigates to MainActivity with the user's data.
     *
     * @param user The authenticated user.
     */
    private void navigateToMainActivity(User user) {
        error.setText("");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", user.getUsername());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("ID", user.getUscID());
        intent.putExtra("password", user.getPassword());
        startActivity(intent);
        finish(); // Optional: Finish LoginActivity so it's removed from the back stack
    }

    /**
     * Navigates to SignUpActivity when the sign-up TextView is clicked.
     *
     * @param view The view that was clicked.
     */
    public void tvSignUpClick(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
