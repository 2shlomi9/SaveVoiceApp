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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import classes.Group;
import classes.User;

public class MemberGroupInfoActivity extends AppCompatActivity {

    Context context;
    ArrayList<Group> list;

    //    private FirebaseStorage storage;
//    private StorageReference storageReference;
    public static String currUid, Uid;
    private String  groupName,groupId;
    private Group group;


    private AlertDialog.Builder groupOptions;
    private AlertDialog options;
    private DatabaseReference group_reference, user_reference;
    Bitmap bitmap;
    private List<String> group_members;
    String participantstr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_group_info);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("Group_ID");
        groupName = intent.getStringExtra("Group_NAME");
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        DatabaseReference ureference = FirebaseDatabase.getInstance().getReference("Users");
        final String[] Rid = new String[1];

        TextView title, managertxt;
        Spinner members;
        Button exitBtn;
        title = findViewById(R.id.tvTitle);
        exitBtn = findViewById(R.id.exitGroup);
        members = findViewById(R.id.tvMembers);
        managertxt = findViewById(R.id.managerIdTv);




        //set title
        title.setText(group.getGroupName());

        //set manager name
        user_reference.child(group.getManagerId()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User manager = snapshot.getValue(User.class);
                if (manager != null){
                    managertxt.setText("Group Manager: \n"+manager.getuserName()+" ("+manager.getFullName()+")");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Set participants
        DatabaseReference referenceD = FirebaseDatabase.getInstance().getReference("Groups").child(group.getGroupId());
        referenceD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group g = snapshot.getValue(Group.class);
                if (g != null) {
                    if (g.getMembers().isEmpty()) {
                        String[] participants = {"no participant"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, participants);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        members.setAdapter(adapter);
                    } else {
                        ArrayList<String> participants = new ArrayList<String>();
                        for (int i = 0; i < g.getMembers().size(); i++) {
                            ureference.child(g.getMembers().get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
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
            public void onCancelled (@NonNull DatabaseError error){

            }
        });



        //Set exit button
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group.remove_member(Uid);
                group_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        group_reference.child(group.getGroupId()).setValue(group);
                        Toast.makeText(context.getApplicationContext(), "exited from group "+group.getGroupName() + " successfully!", Toast.LENGTH_SHORT).show();
                        options.cancel();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("Failed.");
                    }
                });

            }
        });


    }

}
