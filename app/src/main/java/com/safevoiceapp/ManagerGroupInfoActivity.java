package com.safevoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapters.UserParticipantsAdapter;
import classes.Group;
import classes.User;

public class ManagerGroupInfoActivity extends AppCompatActivity {


   private String  groupName,groupId;
   private Context context;
   private DatabaseReference group_reference,user_reference;

   private Group group;

    private UserParticipantsAdapter user_participants_adapter;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialog_builder;
    private ArrayList<User> user_participants;
    private TextView title;
    private Spinner members;
    private ImageView editBtn, deleteBtn, inviteBtn, backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_group_info);

        //set users list
        user_participants = new ArrayList<User>();
        recyclerView = findViewById(R.id.participants_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog_builder = new AlertDialog.Builder(this);
        user_participants_adapter = new UserParticipantsAdapter(this, groupId, user_participants, dialog_builder);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("Group_ID");
        groupName = intent.getStringExtra("Group_NAME");
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        group_reference.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                group = snapshot.getValue(Group.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        context = getApplicationContext();
        title = findViewById(R.id.tvTitle);
        deleteBtn = findViewById(R.id.deleteGroup);
        inviteBtn = findViewById(R.id.Invite);
        editBtn = findViewById(R.id.editBtn);
        backBtn = findViewById(R.id.backBtn);



        //set title
        title.setText(groupName);

        //set back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Set edit button
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerGroupInfoActivity.this , EditGroupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Group_ID",groupId);
                intent.putExtra("Group_NAME", groupName);
                startActivity(intent);

            }
        });

        //Set delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group_reference.child(groupId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user_reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user.getUserGroups().contains(groupId)) {
                                        user.exitGroup(groupId);
                                        user_reference.child(user.getUid()).setValue(user);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                    // Finish current Activity
                Intent intent = new Intent(ManagerGroupInfoActivity.this, ManagerGroupActivity.class);

                // Set flags to clear the stack and start a new task
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Start the new activity
                startActivity(intent);

                // Finish the current activity (EditGroupActivity)
                finish();


            }

        });


        //Set invite button
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ManagerGroupInfoActivity.this , InviteUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Group_ID",groupId);
                startActivity(intent);

           }
       });


    refresh_users();

    }

    private void refresh_users() {

        user_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_participants.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getUserGroups().contains(groupId)) {
                        user_participants.add(user);
                    }
                }
                recyclerView.setAdapter(user_participants_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}