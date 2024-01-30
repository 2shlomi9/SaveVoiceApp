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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import classes.Group;

public class Group_handle {
    private FirebaseFirestore db;
    private CollectionReference groupCollection;
    private DatabaseReference reference;
    private Group group;

    public Group_handle() {

       // mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        groupCollection = db.collection("groups");
    }
    public void addNewGroup(Group group) {
        db.collection("groups")
                .add(group)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Handle successful addition (if needed)
                        Log.d(TAG, "group added to Firestore with ID: " + documentReference.getId());
                        group.setGroupId(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure (if needed)
                        Log.w(TAG, "Error adding group to Firestore", e);
                    }
                });
    }
    public void deleteUser(String userDocID){
        groupCollection.document(userDocID).delete();
    }
    public Group getGroup(String uid){

        groupCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Access the group list from the document
                            reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    group = snapshot.getValue(Group.class);
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

        return group;
    }


}
