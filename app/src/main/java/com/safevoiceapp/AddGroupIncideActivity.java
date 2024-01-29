package com.safevoiceapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddGroupIncideActivity extends AppCompatActivity {

    private String generatedDocumentId, userId ;
    private Group group;
    private User_handle user;
    private EditText editTextGroupName;
    private Button buttonBack;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_incide);

        editTextGroupName = findViewById(R.id.editTextGroupName);
        buttonBack = findViewById(R.id.buttonBack);
        buttonSave = findViewById(R.id.buttonSave);

        group = new Group();


        userId = user.getId();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity on back button click
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGroup();
                finish(); // Close the activity after saving
            }
        });
    }

    private void saveGroup() {
        String groupName = editTextGroupName.getText().toString().trim();

        if (!groupName.isEmpty()) {
            group.createGroup(groupName,userId);
        }

    }

}
