package com.example.promptsharepro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.promptsharepro.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity  extends AppCompatActivity {

    private DatabaseReference database;
    EditText emailET;
    EditText idET;
    EditText usernameET;
    EditText passwordET;
    //    TextView error;
    User matchingUser;
    User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get current user data
        currUser = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String username = extras.getString("username");
            String email = extras.getString("email");
            String id = extras.getString("ID");
            String password = extras.getString("password");
            //make current user
            currUser = new User(username, email, id, password);
        }

        //initialize
        emailET = (EditText) findViewById(R.id.etEmail);
        idET = (EditText) findViewById(R.id.etId);
        usernameET = (EditText) findViewById(R.id.etUsername);
        passwordET = (EditText) findViewById(R.id.etPassword);
//        error = (TextView) findViewById(R.id.signUpError);
        matchingUser = null;

        //get users database
        database = FirebaseDatabase.getInstance().getReference("users");

        //set text to user information
        emailET.setText(currUser.getEmail());
        idET.setText(currUser.getUscID());
        usernameET.setText(currUser.getUsername());
        passwordET.setText(currUser.getPassword());
    }

    public void editEmail(View view) {
        MaterialButton button = (MaterialButton) view;
        //if user clicked to edit
        if(button.getText().toString().equals("Edit")) {
            button.setText("Save");
            emailET.setEnabled(true);
        }

        //if user clicked to save
        if(button.getText().toString().equals("Save")) {
            String email = idET.getText().toString().trim();

            //check if email empty
            if(email.isEmpty()) {
                return;
            }
            //check if username was changed or not
            if(currUser.getEmail().equals(email)) {
                button.setText("Edit");
                emailET.setEnabled(false);
                return;
            }
            //check if usc email
            if(!email.endsWith("usc.edu")) {
//                error.setText("Must be usc email");
                return;
            }

            //check if already in use
            findUser(email, currUser.getUsername(), currUser.getUscID());
            if(matchingUser != null) {
//                error.setText("email aready in use");
                matchingUser = null;
                return;
            }

            //update user information
            currUser.setEmail(email);
            database.child(currUser.getUscID()).setValue(currUser);
            button.setText("Edit");
            emailET.setEnabled(false);
        }
    }

    public void editId(View view) {
        MaterialButton button = (MaterialButton) view;
        //if user clicked to edit
        if(button.getText().toString().equals("Edit")) {
            button.setText("Save");
            idET.setEnabled(true);
        }

        //if user clicked to save
        if(button.getText().toString().equals("Save")) {
            String idStr = idET.getText().toString().trim();

            //check if empty
            if(idStr.isEmpty()) {
                return;
            }

            //check if not changed
            if(currUser.getUscID().equals(idStr)) {
                button.setText("Edit");
                idET.setEnabled(false);
                return;
            }

            //check all values are ints
            int id;
            try{
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
//                error.setText("Invalid ID: (Can Only Contain Numbers)");
                return;
            }

            //check id is 10 digits
            if(idStr.length() != 10){
//                error.setText("Invalid ID: (Must be 10 Digits)");
                return;
            }

            //check if already in use
            findUser(currUser.getEmail(), currUser.getUsername(), idStr);
            if(matchingUser != null) {
//                error.setText("email aready in use");
                matchingUser = null;
                return;
            }

            //update user information (delete and readd)
            String prevID = currUser.getUscID();
            currUser.setUscID(idStr);
            database.child(prevID).removeValue();
            database.child(idStr).setValue(currUser);
            button.setText("Edit");
            idET.setEnabled(false);
        }
    }

    public void editUsername(View view) {
        MaterialButton button = (MaterialButton) view;
        //if user clicked to edit
        if(button.getText().toString().equals("Edit")) {
            button.setText("Save");
            usernameET.setEnabled(true);
        }

        //if user clicked to save
        if(button.getText().toString().equals("Save")) {
            String username = usernameET.getText().toString().trim();

            //if empty
            if(username.isEmpty()) {
                return;
            }

            //check if username was changed or not
            if(currUser.getUsername().equals(username)) {
                button.setText("Edit");
                usernameET.setEnabled(false);
                return;
            }

            //check if already in use
            findUser(currUser.getEmail(), username, currUser.getUscID());
            if(matchingUser != null) {
//                error.setText("email aready in use");
                matchingUser = null;
                return;
            }

            //update user information
            currUser.setUsername(username);
            database.child(currUser.getUscID()).setValue(currUser);
            button.setText("Edit");
            usernameET.setEnabled(false);
        }
    }

    public void editPassword(View view) {
        MaterialButton button = (MaterialButton) view;
        //if user clicked to edit
        if(button.getText().toString().equals("Edit")) {
            button.setText("Save");
            passwordET.setEnabled(true);
        }

        //if user clicked to save
        if(button.getText().toString().equals("Save")) {
            String password = passwordET.getText().toString();

            if(password.isEmpty()) {
                return;
            }

            if(currUser.getPassword().equals(password)) {
                button.setText("Edit");
                passwordET.setEnabled(false);
            }

            currUser.setPassword(password);
            database.child(currUser.getUscID()).setValue(currUser);
            button.setText("Edit");
            passwordET.setEnabled(false);
        }
    }

    public void signOut(View view) {
        //change to login
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
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
                        //if it is still current user info : ignore it
                        if(user.getEmail().equals(email) && !currUser.getEmail().equals(email)) {
                            matchingUser = user;
                            return;
                        } else if(user.getUsername().equals(username) && !currUser.getUsername().equals(username)) {
                            matchingUser = user;
                            return;
                        } else if(user.getUscID().equals(id) && !currUser.getUscID().equals(id)) {
                            matchingUser = user;
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
