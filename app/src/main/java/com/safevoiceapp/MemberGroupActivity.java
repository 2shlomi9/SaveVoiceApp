package com.safevoiceapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapters.GroupAdapterMember;
import classes.Group;
import classes.User;


public class MemberGroupActivity extends AppCompatActivity {

    private ImageView createGroup;
    private ArrayList<Group> memberGroups;

    private DatabaseReference group_reference, user_reference;
    private FirebaseUser fuser;
    private String Uid;
    private GroupAdapterMember groupAdapter;
    private User user;
    // private User_handle user_handle = new User_handle();
    private TextView memberGroupsData, fullname_txt;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialog_builder;

//    private BottomNavigationView bottomNavigationView;
//    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_group);


//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//
//        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                int itemId = item.getItemId();
//                if (itemId == R.id.nav_home) {
//                    startActivity(new Intent(AddGroupActivity.this, MainActivity.class));
//                    return true;
//                } else if (itemId == R.id.nav_group) {
//                    return true;
//                } else if (itemId == R.id.nav_add) {// Start the activity outside of the switch statement
//                    startActivity(new Intent(AddGroupActivity.this, RecordingActivity.class));
//                    return true; // Return true to prevent further processing
//                } else if (itemId == R.id.voice_chat) {
//                    return true;
//                } else if (itemId == R.id.nav_profile) {
//                    return true;
//                }
//
//                if (selectorFragment != null) {
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
//                }
//
//                return true;
//            }
//        });


//
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
//            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
//        } else {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//        }



        memberGroups = new ArrayList<Group>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Uid = fuser.getUid();
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");

        recyclerView = findViewById(R.id.memberGroupsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog_builder = new AlertDialog.Builder(this);
        groupAdapter = new GroupAdapterMember(this, memberGroups, dialog_builder);
        recyclerView.setAdapter(groupAdapter);


        fullname_txt = findViewById(R.id.username_txt);
        memberGroupsData = findViewById(R.id.membersGroup);
        initUI();  // Call initUI here after addGroup is initialized

    }
    private void refresh_memberGroupsData(DataSnapshot snapshot) {
        memberGroups.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Group group = dataSnapshot.getValue(Group.class);
            if(group.getMembers().contains(Uid)) {
                memberGroups.add(group);
            }
        }


    }

    private void initUI() {


        group_reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                refresh_memberGroupsData(snapshot);
                memberGroupsData.setText("groups you manage:\n");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        user_reference.child(Uid).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null){
                    fullname_txt.setText("Hello "+user.getFullName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }


}
