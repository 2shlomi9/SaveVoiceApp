package com.safevoiceapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapters.GroupAdapterManager;
import classes.Group;
import classes.User;


public class ManagerGroupActivity extends AppCompatActivity {

    private ImageView createGroup;
    private ArrayList<Group> managerGroups;

    private DatabaseReference group_reference, user_reference;
    private FirebaseUser fuser;
    private String Uid;
    private GroupAdapterManager groupAdapter;
    private User user;
   // private User_handle user_handle = new User_handle();

    private RecyclerView recyclerView;
    private AlertDialog.Builder dialog_builder;

    private Button viewBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_group);
        viewBtn = findViewById(R.id.viewBtn);

        managerGroups = new ArrayList<Group>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Uid = fuser.getUid();
        createGroup = findViewById(R.id.btnCreateGroup);
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");

        recyclerView = findViewById(R.id.managedGroupsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog_builder = new AlertDialog.Builder(this);
        groupAdapter = new GroupAdapterManager(this, managerGroups, dialog_builder);
        recyclerView.setAdapter(groupAdapter);


        initUI();  // Call initUI here after addGroup is initialized

    }
    private void refresh_managerGroupsData(DataSnapshot snapshot) {
        managerGroups.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Group group = dataSnapshot.getValue(Group.class);
            if(group.getManagerId().equals(Uid)) {
                managerGroups.add(group);
            }
        }


    }

    private void initUI() {


        group_reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                refresh_managerGroupsData(snapshot);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewBtn.setVisibility(View.GONE);
            }

        });
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerGroupActivity.this,
                        AddGroupActivity.class);
                startActivity(intent);
            }

        });



    }


}
