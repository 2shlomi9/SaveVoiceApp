package com.safevoiceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddGroupIncideActivity extends AppCompatActivity {

    private EditText editTextGroupName;
    private Button buttonBack;
    private Button buttonSave;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_incide);

        editTextGroupName = findViewById(R.id.editTextGroupName);
        buttonBack = findViewById(R.id.buttonBack);
        buttonSave = findViewById(R.id.buttonSave);

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
            // Save the group name to the "groups" collection in Firestore
            // You can replace "groups" with your actual collection name

            Map<String, Object> groupData = new HashMap<>();
            groupData.put("groupName", groupName);


            db.collection("groups").add(groupData);
        }
        // Handle empty group name if needed
    }
}
