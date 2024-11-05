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

public class SignUpActivity extends AppCompatActivity {

    private DatabaseReference database;
    EditText emailET;
    EditText idET;
    EditText usernameET;
    EditText passwordET;
    TextView error;
    User matchingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //initialize
        emailET = (EditText) findViewById(R.id.etSignUpEmail);
        idET = (EditText) findViewById(R.id.etSignUpUSCid);
        usernameET = (EditText) findViewById(R.id.etSignUpUserName);
        passwordET = (EditText) findViewById(R.id.etSignUpPassword);
        error = (TextView) findViewById(R.id.signUpError);
        matchingUser = null;

        //get users database
        database = FirebaseDatabase.getInstance().getReference("users");
    }

    public void signUp(View view) {
        String email = emailET.getText().toString().trim();
        String idStr = idET.getText().toString().trim();
        String username = usernameET.getText().toString().trim();
        String password = passwordET.getText().toString();
        int id;

        //check if any field is not filled in
        if(email.isEmpty() || idStr.isEmpty() || username.isEmpty() || password.isEmpty()) {
            error.setText("Fill in all feilds");
            return;
        }

        //check all values are ints
        try{
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            error.setText("Invalid ID: (Can Only Contain Numbers)");
            return;
        }

        //check id is 10 digits
        if(idStr.length() != 10){
            error.setText("Invalid ID: (Must be 10 Digits)");
            return;
        }

        //check if usc email
        if(!email.endsWith("usc.edu")){
            error.setText("Must be usc email");
            return;
        }

        //check to see if usc email or username is in database
        findUser(email, username, idStr);

        //check if account exists or not
        if(matchingUser != null) {
            //email exists
            if(matchingUser.getEmail().equals(email)) {
                error.setText("Account already exists");
                matchingUser = null;
                return;
            }

            //id exists
            if(matchingUser.getUscID().equals(id)) {
                error.setText("ID already in use");
                matchingUser = null;
                return;
            }

            //username exists
            if(matchingUser.getUsername().equals(username)) {
                error.setText("Username already exists");
                matchingUser = null;
                return;
            }
        }

        //add user to database
        User newUser = new User(email, username, idStr, password);
        database.child(idStr).setValue(newUser);

        //change to main screen
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra("username", newUser.getUsername());
        intent.putExtra("email", newUser.getEmail());
        intent.putExtra("ID", newUser.getUscID());
        intent.putExtra("password", newUser.getPassword());
        startActivity(intent);
    }

    private void findUser(String email, String username, String id) {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        //check if it's the user email or username is already used
                        if(user.getEmail().equals(email) || user.getUsername().equals(username) || user.getUscID().equals(id)) {
                            matchingUser = user;
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignUpActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void tvLoginClick(View view) {
        //change to login
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
