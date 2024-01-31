package com.safevoiceapp;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import classes.User;

public class registerActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private EditText first_nameTextView, last_nameTextView, user_nameTextView;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private TextView loginUser;




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
        btnRegister = findViewById(R.id.register);
        loginUser = findViewById(R.id.login_user);
        initUI();

    }
    private void initUI() {
        // Set on Click Listener on Registration button
        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registerActivity.this, loginActivity.class));

            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
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
        User newUser = new User("",first_name, last_name, user_name, email);


        String validationMessage;
        // Validations for input email and password
        validationMessage = User.check_name(first_name);
        if (!validationMessage.equals("accept")) {
            Toast.makeText(getApplicationContext(),
                            validationMessage,
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        validationMessage = User.check_name(last_name);
        if (!validationMessage.equals("accept")) {
            Toast.makeText(getApplicationContext(),
                            validationMessage,
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        validationMessage = User.check_username(user_name);
        if (!validationMessage.equals("accept")) {
            Toast.makeText(getApplicationContext(),
                            validationMessage,
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        validationMessage = User.check_email(email);
        if(!validationMessage.equals("accept")){
            Toast.makeText(getApplicationContext(),
                            validationMessage,
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        validationMessage = User.check_pass(password);
        if (!validationMessage.equals("accept")) {
            Toast.makeText(getApplicationContext(),
                            validationMessage,
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        register(newUser,password);


    }

    private void register(User user, String password) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(registerActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    user.setUid(Uid);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(Uid)
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
//                                        FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
//                                        user2.sendEmailVerification();
                                        Intent n = new Intent(getApplicationContext(),StartActivity.class);
                                        startActivity(n);
                                    } else {
                                        Toast.makeText(registerActivity.this, " Registration failed!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }
                else {
                    Toast.makeText(registerActivity.this, " Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}


