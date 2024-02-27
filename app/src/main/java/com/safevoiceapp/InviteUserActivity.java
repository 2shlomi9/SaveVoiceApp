package com.safevoiceapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import adapters.UserInviteAdapter;
import classes.Group;
import classes.Record;
import classes.User;

public class InviteUserActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private MediaRecorder mediaRecorder;
    private String audioFile = null;
    private MediaPlayer mediaPlayer;


    private boolean isRecording = false;
    private Button stopRecordingButton;

    // Firebase Storage
    private StorageReference storageReference;
    private StorageReference storageRef;

    private String userId;


    private ImageView serachButton;
    private TextView groupTitle;
    private RelativeLayout editRecord;


    private boolean isPlaying = false;
    private CountDownTimer countDownTimer;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference ureference, group_reference, record_reference;
    private EditText messageEt;
    private ArrayList<String> managerGroups_id, managerGroups_names;
    private String groupId, groupName, Uid;
    private UserInviteAdapter user_invite_adapter;
    private EditText userSearch;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialog_builder;
    private ArrayList<User> user_invite;
    private Context context = this;
    ArrayList<Group> list;

    private AlertDialog.Builder groupOptions;
    private String userName;
    private AlertDialog options;
    private DatabaseReference user_reference;
    String participantstr = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_user);

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();


        serachButton = findViewById(R.id.search_user_btn);
        userSearch = findViewById(R.id.seach_username_input);
        managerGroups_id = new ArrayList<String>();
        managerGroups_names = new ArrayList<String>();
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Intent intent = getIntent();
        groupId = intent.getStringExtra("Group_ID");
        groupName = intent.getStringExtra("Group_NAME");
        //set users list
        user_invite = new ArrayList<User>();
        record_reference = FirebaseDatabase.getInstance().getReference("Records");
        recyclerView = findViewById(R.id.search_user_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog_builder = new AlertDialog.Builder(this);
        user_invite_adapter = new UserInviteAdapter(this, groupId, user_invite, dialog_builder);
        initUI();

    }

    private void refresh_users() {

        user_reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_invite.clear();
                if (!TextUtils.isEmpty(userSearch.getText())) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (!user.getUid().equals(Uid) && !user.getUserGroups().contains(groupId) && (user.getuserName().startsWith(userSearch.getText().toString()) || user.getFullName().startsWith(userSearch.getText().toString()))) {
                            user_invite.add(user);
                        }
                    }
                }
                recyclerView.setAdapter(user_invite_adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void initUI() {
        //refresh_users();
        serachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh_users();
            }
        });

    }
}
