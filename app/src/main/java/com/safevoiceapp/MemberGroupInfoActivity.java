package com.safevoiceapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapters.UserParticipantsAdapter;
import classes.Group;
import classes.User;

public class MemberGroupInfoActivity extends AppCompatActivity {

    private String groupName, groupId;
    private Context context;
    private DatabaseReference group_reference, user_reference;

    private Group group;

    private UserParticipantsAdapter user_participants_adapter;
    private String Uid;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialog_builder;
    private ArrayList<User> user_participants;
    private TextView title;
    private Spinner members;
    private ImageView exitBtn, backBtn;
    private EditText userInviteTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_group_info);

        //set users list
        user_participants = new ArrayList<User>();
        recyclerView = findViewById(R.id.participants_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialog_builder = new AlertDialog.Builder(this);
        user_participants_adapter = new UserParticipantsAdapter(this, groupId, user_participants, dialog_builder);
        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        exitBtn = findViewById(R.id.exitGroup);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //set title
        title.setText(groupName);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group.remove_member(Uid);
                group_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        group_reference.child(group.getGroupId()).setValue(group);
                        user_reference.child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                user.exitGroup(groupId);
                                user_reference.child(Uid).setValue(user);
                                Toast.makeText(context.getApplicationContext(), "exited from group "+group.getGroupName() + " successfully!", Toast.LENGTH_SHORT).show();
                                refresh_users();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Failed.");
                    }
                });
                // Finish current Activity
                Intent intent = new Intent(MemberGroupInfoActivity.this, MemberGroupActivity.class);

                // Set flags to clear the stack and start a new task
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Start the new activity
                startActivity(intent);

                // Finish the current activity (EditGroupActivity)
                finish();


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










