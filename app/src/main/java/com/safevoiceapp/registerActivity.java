package com.safevoiceapp;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import classes.User;

public class registerActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private EditText first_nameTextView, last_nameTextView, user_nameTextView;
    private Button Btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private TextView loginUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User_handle user;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        first_nameTextView = findViewById(R.id.firstname);
        last_nameTextView = findViewById(R.id.lastname);
        user_nameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        Btn = findViewById(R.id.register);
        loginUser = findViewById(R.id.login_user);
        user = new User_handle();

   //     pd = new ProgressDialog(this);
        // Set on Click Listener on Registration button
        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registerActivity.this, loginActivity.class));

            }
        });
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerNewUser();
            }
        });
    }



    private void registerNewUser()
    {

        // Take the value of two edit texts in Strings
        String email, password,first_name, last_name, user_name;
        first_name = first_nameTextView.getText().toString();
        last_name = last_nameTextView.getText().toString();
        user_name = user_nameTextView.getText().toString();
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        String validationMessage;
        // Validations for input email and password
        validationMessage = User.check_email(email);
        if(validationMessage.equals("accept")){
            Toast.makeText(getApplicationContext(),
                            validationMessage,
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        validationMessage = User.check_pass(password);
        if (validationMessage.equals("accept")) {
            Toast.makeText(getApplicationContext(),
                            validationMessage,
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // create new user or register new user
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();


                            // if the user created intent to login activity
                            Intent intent
                                    = new Intent(registerActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            User newUser = new User(first_name, last_name, user_name, email);
                            user.addNewUser(newUser);
                        }
                        else {

                            // Registration failed
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Registration failed!!"
                                                    + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();

                        }
                    }
                });
    }




}
