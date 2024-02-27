package com.safevoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import classes.Group;
import classes.User;

public class EditGroupActivity extends AppCompatActivity {
    private String groupId;
    private String groupName;
    private String newGroupName;
    private EditText editTextGroupName;
    private Button editBtn;
    private ImageView backBtn;
    private DatabaseReference group_reference,user_reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        Intent intent = getIntent();
        groupId = intent.getStringExtra("Group_ID");
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        editTextGroupName = findViewById(R.id.editTextGroupName);
        editBtn = findViewById(R.id.editBtn);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGroupName();;
                // Create an intent to go back to ManagerGroupActivity
                Intent intent = new Intent(EditGroupActivity.this, ManagerGroupActivity.class);

                // Set flags to clear the stack and start a new task
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Start the new activity
                startActivity(intent);

                // Finish the current activity (EditGroupActivity)
                finish();
            }
        });

    }




        private void createNewGroupName () {
            groupName = editTextGroupName.getText().toString().trim();
            renameGroup(groupId, groupName);

        }
        private void renameGroup (String groupId, String newGroupName){
            // Create a map to update the specific value
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("groupName", newGroupName);
            group_reference.child(groupId).updateChildren(updateMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditGroupActivity.this, " Name Change successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });

        }



}