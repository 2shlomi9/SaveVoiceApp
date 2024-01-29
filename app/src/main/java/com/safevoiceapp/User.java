package com.safevoiceapp;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


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

public class User {
    private ArrayList<String> userMangerGroups;

    private ArrayList<String> userGroups;

    private String userName;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private CollectionReference usersCollection;

    private void initUI() {

        mAuth = FirebaseAuth.getInstance();
        usersCollection = db.collection("users");

    }
    public void addNewUser(String name, String email){
        Map<String, Object> user = new HashMap<>();
        user.put("id",mAuth.getUid());
        user.put("name", name);
        user.put("email", email);
        user.put("groups", userGroups);  // Initialize with an empty list
        user.put("MangerGroup", userMangerGroups);  // Initialize with an empty list
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Handle successful addition (if needed)
                        Log.d(TAG, "User added to Firestore with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure (if needed)
                        Log.w(TAG, "Error adding user to Firestore", e);
                    }
                });
    }
    public void deleteUser(String userDocID){
        usersCollection.document(userDocID).delete();
    }



    public String getId(){
        return mAuth.getUid();
    }

    public String getName(){
        usersCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the group list from the document
                            userName = (String) document.get("name");
                        }
                    } else {
                        Log.w("TAG", "Error getting documents: ", task.getException());
                    }
                });

        return userName;
    }
    public ArrayList<String> getUserManagerGroups() {

        usersCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the group list from the document
                            ArrayList<String> documentGroups = (ArrayList<String>) document.get("MangerGroup");
                            userMangerGroups.addAll(documentGroups);
                        }
                    } else {
                        Log.w("TAG", "Error getting documents: ", task.getException());
                    }
                });

        return userMangerGroups;
    }
    public ArrayList<String> getUserGroups() {

        usersCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the group list from the document
                            ArrayList<String> documentGroups = (ArrayList<String>) document.get("groups");
                            userGroups.addAll(documentGroups);
                        }
                    } else {
                        Log.w("TAG", "Error getting documents: ", task.getException());
                    }
                });

        return userGroups;
    }
    public void addMangerGroups(String groupID){
        userMangerGroups.add(groupID);
    }
    public void deleteMangerGroups(String groupID){
        userMangerGroups.remove(groupID);
    }
    public void addGroups(String groupID){
        userGroups.add(groupID);

    }
    public void deleteGroups(String groupID){
        userGroups.remove(groupID);

    }

}
