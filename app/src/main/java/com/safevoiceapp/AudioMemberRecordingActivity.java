package com.safevoiceapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

import adapters.ReceiveRecordAdapter;
import adapters.SentRecordAdapter;
import classes.Group;
import classes.Record;

public class AudioMemberRecordingActivity extends AppCompatActivity {


    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private MediaRecorder mediaRecorder;
    private String audioFile = null;

    private boolean isRecording = false;
    private Button stopRecordingButton;

    // Firebase Storage
    private StorageReference storageReference;
    private StorageReference storageRef;

    private String generatedDocumentId, userId, audioUrl;

    private String audioFilePath;

    private ImageView send_button ,deleteButton,listenAgainButton,sendButton, groupInfo;
    private TextView groupTitle;
    private RelativeLayout editRecord;
    private CountDownTimer countDownTimer;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference ureference, group_reference, record_reference;
    private EditText messageEt;
    private ArrayList<String> memberGroups_id, memberGroups_names;
    private String groupId,groupName, Uid;
    private SentRecordAdapter sent_record_adapter;
    private ReceiveRecordAdapter receive_record_adapter;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialog_builder;
    private ArrayList<Record> receive_records;
    private Context context = this;
    ArrayList<Group> list;


    public static String currUid;
    private AlertDialog.Builder groupOptions;
    private AlertDialog options;
    private DatabaseReference user_reference;
    String participantstr = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_options_member);

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        groupTitle = findViewById(R.id.tvTitle);
        send_button = findViewById(R.id.btnSend);
        groupInfo = findViewById(R.id.groupInfo);
        messageEt = findViewById(R.id.msgText);
        memberGroups_id = new ArrayList<String>();
        memberGroups_names = new ArrayList<String>();
        ureference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        record_reference = FirebaseDatabase.getInstance().getReference("Records");
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Intent intent = getIntent();
        groupId = intent.getStringExtra("Group_ID");
        groupName = intent.getStringExtra("Group_NAME");
        //set records list
        receive_records = new ArrayList<Record>();
        record_reference = FirebaseDatabase.getInstance().getReference("Records");
        recyclerView = findViewById(R.id.recordsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog_builder = new AlertDialog.Builder(this);
        receive_record_adapter = new ReceiveRecordAdapter(this,receive_records,dialog_builder);

        groupTitle.setText(groupName);
        groupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , MemberGroupInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Group_ID", groupId);
                intent.putExtra("Group_NAME", groupName);
                context.startActivity(intent);

            }
        });

        group_reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                refresh_menagerGroupsData(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        record_reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receive_records.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Record record = dataSnapshot.getValue(Record.class);
                    if((record.getSent_users().contains(Uid) || record.getDelivered_users().contains(userId)) && record.getGroupId().equals(groupId)) {
                        receive_records.add(record);
                    }
                }

                recyclerView.setAdapter(receive_record_adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, get their UID
            userId = Uid;
        }
        initUI();
    }

    private void refresh_menagerGroupsData(DataSnapshot snapshot) {
        memberGroups_names.clear();
        memberGroups_id.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Group group = dataSnapshot.getValue(Group.class);
            if (group.getMembers().contains(Uid)) {
                memberGroups_id.add(group.getGroupId());
                memberGroups_names.add(group.getGroupName());
            }
        }

    }


    private void initUI() {


    }



}
