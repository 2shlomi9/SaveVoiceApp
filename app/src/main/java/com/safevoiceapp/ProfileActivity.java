package com.safevoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import classes.User;

public class ProfileActivity extends AppCompatActivity {
    private TextView fullNameTv,userNameTv,emailTv;
    private User profile;
    private String Uid;
    private FirebaseUser fuser;


    private DatabaseReference group_reference, user_reference;
    private Button logout,safeMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logout = findViewById(R.id.logout);
        safeMode = findViewById(R.id.safeMode);
        fullNameTv =findViewById(R.id.tvFullName);
        userNameTv =findViewById(R.id.tvUserName);
        emailTv = findViewById(R.id.tvEmail);
        this.user_reference = FirebaseDatabase.getInstance().getReference("Users");
        this.group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Uid = fuser.getUid();

        user_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user.getUid().equals(Uid)){
                        profile = user;
                        if (profile!=null){
                            emailTv.setText(profile.getEmail());
                            userNameTv.setText(profile.getuserName());
                            fullNameTv.setText(profile.getFullName());
                        }
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        safeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MediaPlayerActivity.class));
            }
        });






        //logout .................................................
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfileActivity.this,"Logged out!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, StartActivity.class));
                finish();
            }
        });
    }
}