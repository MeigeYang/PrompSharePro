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

        //initialize
        emailET = (EditText) findViewById(R.id.etLoginEmail);
        passwordET = (EditText) findViewById(R.id.etLoginPassword);
        error = (TextView) findViewById(R.id.loginError);
        error.setText("");
        matchingUser = null;

        //get users database
        database = FirebaseDatabase.getInstance().getReference("users");
    }

    public void logIn(View view) throws InterruptedException {
        //get user inputs
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString();

        //check if any field is not filled in
        if(email.isEmpty() || password.isEmpty()) {
            error.setText("Fill in all feilds");
            return;
        }

        //check if account exists
        findUser(email);

        //function didn't run properly
        if(matchingUser == null)
        {
//            error.setText("null");
            return;
        }
        //account not found
        if(matchingUser.getEmail().isEmpty())
        {
            error.setText("Email Not Found");
            return;
        }

        //compare if valid password
        if(!matchingUser.getPassword().equals(password)) {
            error.setText("Password Doesn't match");
            matchingUser = null;
            return;
        }

        error.setText("");
        //set user to current user
        User currUser = matchingUser;

        //change to main screen
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        //pass through current user data
        intent.putExtra("username", currUser.getUsername());
        intent.putExtra("email", currUser.getEmail());
        intent.putExtra("ID", currUser.getUscID());
        intent.putExtra("password", currUser.getPassword());
        startActivity(intent);
    }

    private void findUser(String email) {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        //check if it's the user signing in
                        if(user.getEmail().equals(email)) {
                            matchingUser = user;
                            return;
                        }
                    }
                }
                if(matchingUser == null) {
                    matchingUser = new User("", "", "", "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void tvSignUpClick(View view) {
        //change to sign up
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
