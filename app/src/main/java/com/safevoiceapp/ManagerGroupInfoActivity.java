package com.safevoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import classes.Group;
import classes.User;

public class ManagerGroupInfoActivity extends AppCompatActivity {


   private String  groupName,groupId;
   private Context context;
   private DatabaseReference group_reference,user_reference;

   private Group group;


    private TextView title;
    private Spinner members;
    private Button editBtn, deleteBtn, inviteBtn;
    private EditText userInviteTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_group_info);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("Group_ID");
        groupName = intent.getStringExtra("Group_NAME");
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        group_reference.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                group = snapshot.getValue(Group.class);
                //Set participants
                if (group != null) {
                    if (group.getMembers().isEmpty()) {
                        String[] participants = {"no participant"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, participants);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        members.setAdapter(adapter);
                    } else {
                        ArrayList<String> participants = new ArrayList<String>();
                        for (int i = 0; i < group.getMembers().size(); i++) {

                            user_reference.child(group.getMembers().get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User profile = snapshot.getValue(User.class);
                                    if (profile != null) {
                                        participants.add(profile.getFullName());
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, participants);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        members.setAdapter(adapter);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        context = getApplicationContext();
        title = findViewById(R.id.tvTitle);
        deleteBtn = findViewById(R.id.deleteGroup);
        inviteBtn = findViewById(R.id.Invite);
        editBtn = findViewById(R.id.editGroup);
        members = findViewById(R.id.tvMembers);
        userInviteTxt = findViewById(R.id.etUserName);

        //set title
        title.setText(groupName);


        //Set invite button
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = userInviteTxt.getText().toString();
                user_reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userId="";
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            assert user != null;
                            if (user.getuserName().equals(userName)) {
                                userId = user.getUid();
                            }
                        }
                        if(userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Toast.makeText(context, "You can't invite yourself to the group.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(group.getMembers().contains(userId)){
                            Toast.makeText(context, userName+" already member in "+groupName, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(userId.equals("")){
                            Toast.makeText(context, userName+" is not exists.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        group.addMembers(userId);
                        group_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                group_reference.child(group.getGroupId()).setValue(group);
                                Toast.makeText(context.getApplicationContext(), "The invitation sent to "+userName + " successfully!", Toast.LENGTH_SHORT).show();
                                userInviteTxt.setText("");
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Failed.");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });




    }
}