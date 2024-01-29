package com.safevoiceapp;

import androidx.annotation.NonNull;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group {

    private ArrayList<String> Members;
    private String menagerID,generatedDocumentId;
    private String groupID;
    private String groupName;
    private Map<String,Object> groupData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private CollectionReference groupsCollection;

    private void initUI() {

        mAuth = FirebaseAuth.getInstance();
        groupsCollection = db.collection("groups");
        groupData = new HashMap<>();

    }


    public ArrayList<String> getUserManagerGroups() {

        groupsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the group list from the document
                            ArrayList<String> documentGroups = (ArrayList<String>) document.get("members");
                            Members.addAll(documentGroups);
                        }
                    } else {
                        Log.w("TAG", "Error getting documents: ", task.getException());
                    }
                });

        return Members;
    }

    public String getGroupName(){

        groupsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the group list from the document
                            groupName = (String) document.get("groupName");
                        }
                    } else {
                        Log.w("TAG", "Error getting documents: ", task.getException());
                    }
                });

        return groupName;
    }

    public String getIdMenger(){

        groupsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the group list from the document
                            menagerID = (String) document.get("IdMenger");
                        }
                    } else {
                        Log.w("TAG", "Error getting documents: ", task.getException());
                    }
                });

        return menagerID;
    }

    public String getGroupID(){

        groupsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the group list from the document
                            groupID = (String) document.get("groupId");
                        }
                    } else {
                        Log.w("TAG", "Error getting documents: ", task.getException());
                    }
                });

        return groupID;
    }



    public void createGroup(String name,String userId){



            // Save the group name to the "groups" collection in Firestore
            // You can replace "groups" with your actual collection name

            groupData.put("groupName", name);
            groupData.put("mengerId", userId);
            groupData.put("members", Members);



            groupsCollection.add(groupData)
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


    public void deleteGroup(){

    }

    public void addMember(String userID){
        Members.add(userID);
    }
    public void deleteMember(String userID){
        Members.remove(userID);
    }


}
