package com.safevoiceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGroupIncideActivity extends AppCompatActivity {

    private String generatedDocumentId, userId ;
    private EditText editTextGroupName;
    private Button buttonBack;
    private Button buttonSave;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionRef;

    private Map<String, Object> groupData;
    private List<String> members;

   // private DatabaseOperations databaseOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_incide);

        editTextGroupName = findViewById(R.id.editTextGroupName);
        buttonBack = findViewById(R.id.buttonBack);
        buttonSave = findViewById(R.id.buttonSave);
        groupData = new HashMap<>();
        collectionRef = db.collection("groups");
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        members = new ArrayList<>();
        //databaseOperations = new DatabaseOperations();

        if (currentUser != null) {
            // User is signed in, get their UID
            userId = currentUser.getUid();
        }
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

            groupData.put("groupName", groupName);
            groupData.put("mengerId", userId);
            groupData.put("members", members);



            collectionRef.add(groupData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Document added with generated ID
                            generatedDocumentId = documentReference.getId();
                            Log.d("Firestore", "Document added with ID: " + generatedDocumentId);

                            // Now, add the generated ID to your user data HashMap
                            groupData.put("groupId", generatedDocumentId);

                            // Update the document with the firestoreId
                            documentReference.update("groupId", generatedDocumentId)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Firestore", "groupId updated successfully");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Firestore", "Error updating groupId", e);
                                        }
                                    });

                            // Print the userData with the Firestore-generated ID
                            Log.d("Firestore", "User data with Firestore ID: " + groupData.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error adding document", e);
                        }
                    });
        }

        // Handle empty group name if needed
    }

}
