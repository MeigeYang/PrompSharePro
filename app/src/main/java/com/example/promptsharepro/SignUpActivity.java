package com.example.promptsharepro;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.promptsharepro.model.User;

public class SignUpActivity extends AppCompatActivity {

    public void signUp(View view) {
        setContentView(R.layout.activity_signup); //temp thing to get rid of errors (Remove later)
        EditText emailET = (EditText) findViewById(R.id.etSignUpEmail);
        EditText idET = (EditText) findViewById(R.id.etSignUpUSCid);
        EditText usernameET = (EditText) findViewById(R.id.etSignUpUserName);
        EditText passwordET = (EditText) findViewById(R.id.etSignUpPassword);
        TextView error = (TextView) findViewById(R.id.signUpError);
        String email = emailET.getText().toString();
        String idStr = idET.getText().toString();
        String username = usernameET.getText().toString();
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

        //check if account exists or not
//        check to see if usc email is in database
//        if(email exists) {
//            error.setText("Account already exists");
//            return;
//        }

        //check if username is already used
//        check if username exists in database
//        if(username exists) {
//            error.setText("Username already exists");
//            return;
//        }

        User newUser = new User(email, username, idStr);
//        add user to database
//        set current user as newUser
        setContentView(R.layout.activity_main);
    }

    public void tvLoginClick(View view) {
        setContentView(R.layout.activity_login);
    }
}
