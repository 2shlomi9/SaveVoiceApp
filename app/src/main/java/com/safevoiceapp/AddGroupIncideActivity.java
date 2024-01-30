package com.safevoiceapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.UUID;

import classes.Group;

public class AddGroupIncideActivity extends AppCompatActivity {

    private String userId;
    private EditText editTextGroupName;
    private Button buttonBack;
    private Button buttonSave;
    private Group_handle groupHendle;
    private User_handle userHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_incide);

        groupHendle = new Group_handle(); // Assuming Group_handle has a default constructor
        userHandle = new User_handle();

        editTextGroupName = findViewById(R.id.editTextGroupName);
        buttonBack = findViewById(R.id.buttonBack);
        buttonSave = findViewById(R.id.buttonSave);
        userId = userHandle.getId();
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
        String groupName, groupId,managerId;
        groupName = editTextGroupName.getText().toString().trim();
        managerId = userId;
        if (!groupName.isEmpty()) {
            groupId = generateGroupId();
            Group newGroup = new Group(groupName, groupId, managerId);
            groupHendle.addNewGroup(newGroup);
        }
    }
    public static String generateGroupId() {
        // Generate a random UUID
        UUID uuid = UUID.randomUUID();

        // Convert the UUID to a string and remove dashes
        return uuid.toString().replace("-", "");
    }

}
