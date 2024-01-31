package com.safevoiceapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.content.ContextCompat.startActivity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import classes.Group;

public class Group_handle extends AppCompatActivity {
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
                        String groupId = documentReference.getId();
                        group.setGroupId(groupId);
                        documentReference.set(group);
                        // Handle successful addition (if needed)
                        Log.d(TAG, "group added to Firestore with ID: " + documentReference.getParent().getId());

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
    public void deleteUser(String groupDocID){
        groupCollection.document(groupDocID).delete();
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
