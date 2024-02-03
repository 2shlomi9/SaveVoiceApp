package com.safevoiceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import classes.Group;

public class AddGroupActivity extends AppCompatActivity {

    private String userId;
    private EditText editTextGroupName, editTextDescription;
    private Button buttonBack;
    private Button buttonSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        editTextGroupName = findViewById(R.id.editTextGroupName);
        editTextDescription = findViewById(R.id.editTextGroupDescription);
        buttonBack = findViewById(R.id.buttonBack);
        buttonSave = findViewById(R.id.buttonSave);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        initUI();
    }
    private void initUI() {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity on back button click
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
                finish(); // Close the activity after saving
            }
        });

    }

    private void createGroup() {
        String groupName, groupId,managerId,description;
        groupName = editTextGroupName.getText().toString().trim();
        description = editTextDescription.getText().toString().trim();
        managerId = userId;
        if (!groupName.isEmpty()) {
            groupId = "";
            Group newGroup = new Group(groupName, groupId, managerId, description);
            submitGroup(newGroup);
        }
    }
    private void submitGroup(Group group) {
        String Uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups").push();
        String Gid=reference.getKey();
        group.setGroupId(Gid);
        reference.setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(AddGroupActivity.this, group.getGroupName() + " submitted successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddGroupActivity.this, ManagerGroupActivity.class));
                }
                else
                    Toast.makeText(AddGroupActivity.this, "Submission failed!", Toast.LENGTH_SHORT).show();
            }
        });


    }
//    public static String generateGroupId() {
//        // Generate a random UUID
//        UUID uuid = UUID.randomUUID();
//
//        // Convert the UUID to a string and remove dashes
//        return uuid.toString().replace("-", "");
//    }

}
