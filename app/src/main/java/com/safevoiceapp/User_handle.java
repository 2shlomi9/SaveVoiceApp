package com.safevoiceapp;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import androidx.annotation.NonNull;


import android.util.Log;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import classes.User;

public class User_handle {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private CollectionReference usersCollection;
    private DatabaseReference reference;
    private User user;

    private void initUI() {

        mAuth = FirebaseAuth.getInstance();

        usersCollection = db.collection("users");

    }
    public void addNewUser(User user){
        initUI();

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

    public User getUser(){
        String uid =getId();

        usersCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the group list from the document
                            reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    user = snapshot.getValue(User.class);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    } else {
                        Log.w("TAG", "Error getting documents: ", task.getException());
                    }
                });

        return user;
    }
//    public ArrayList<String> getUserManagerGroups() {
//
//        usersCollection.get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            // Access the group list from the document
//                            ArrayList<String> documentGroups = (ArrayList<String>) document.get("MangerGroup");
//                            userMangerGroups.addAll(documentGroups);
//                        }
//                    } else {
//                        Log.w("TAG", "Error getting documents: ", task.getException());
//                    }
//                });
//
//        return userMangerGroups;
//    }
//    public ArrayList<String> getUserGroups() {
//
//        usersCollection.get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            // Access the group list from the document
//                            ArrayList<String> documentGroups = (ArrayList<String>) document.get("groups");
//                            userGroups.addAll(documentGroups);
//                        }
//                    } else {
//                        Log.w("TAG", "Error getting documents: ", task.getException());
//                    }
//                });
//
//        return userGroups;
//    }
//    public void addMangerGroups(String groupID){
//        userMangerGroups.add(groupID);
//    }
//    public void deleteMangerGroups(String groupID){
//        userMangerGroups.remove(groupID);
//    }
//    public void addGroups(String groupID){
//        userGroups.add(groupID);
//
//    }
//    public void deleteGroups(String groupID){
//        userGroups.remove(groupID);
//
//    }

}
