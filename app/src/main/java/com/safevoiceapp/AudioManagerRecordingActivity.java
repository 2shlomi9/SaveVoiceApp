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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.IOException;
import java.util.ArrayList;

import adapters.SentRecordAdapter;
import classes.Record;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import classes.Group;


public class AudioManagerRecordingActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {


    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private MediaRecorder mediaRecorder;
    private String audioFile = null;
    private MediaPlayer mediaPlayer;


    private boolean isRecording = false;
    private Button stopRecordingButton;

    // Firebase Storage
    private StorageReference storageReference;
    private StorageReference storageRef;

    private String generatedDocumentId, userId, audioUrl;

    private String audioFilePath;

    private ImageView recordButton ,deleteButton,listenAgainButton,sendButton,backBtn, groupInfo;
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
    private String groupId,groupName, Uid;
    private SentRecordAdapter sent_record_adapter;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialog_builder;
    private ArrayList<Record> sent_records;
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
        setContentView(R.layout.group_options_manager);

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        groupTitle = findViewById(R.id.tvTitle);
        recordButton = findViewById(R.id.btnRecord);
        recordButton.setOnClickListener(this);
        editRecord = findViewById(R.id.editRecord);
        backBtn = findViewById(R.id.backBtn);
        // recordingDuration = findViewById(R.id.tvRecordingDuration);
        deleteButton = findViewById(R.id.btnDelete);
        groupInfo = findViewById(R.id.groupInfo);
        messageEt = findViewById(R.id.msgText);
        listenAgainButton = findViewById(R.id.btnListenAgain);
        //sendButton = findViewById(R.id.btnSend);
        managerGroups_id = new ArrayList<String>();
        managerGroups_names = new ArrayList<String>();
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
        sent_records = new ArrayList<Record>();
        record_reference = FirebaseDatabase.getInstance().getReference("Records");
        recyclerView = findViewById(R.id.recordsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog_builder = new AlertDialog.Builder(this);
        sent_record_adapter = new SentRecordAdapter(this, sent_records, dialog_builder);

        groupTitle.setText(groupName);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        groupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ManagerGroupInfoActivity.class);
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
                sent_records.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Record record = dataSnapshot.getValue(Record.class);
                    if(record.getSenderId().equals(Uid) && record.getGroupId().equals(groupId)) {
                        sent_records.add(record);
                    }
                }

                recyclerView.setAdapter(sent_record_adapter);

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
        managerGroups_names.clear();
        managerGroups_id.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Group group = dataSnapshot.getValue(Group.class);
            if (group.getManagerId().equals(Uid)) {
                managerGroups_id.add(group.getGroupId());
                managerGroups_names.add(group.getGroupName());
            }
        }

    }
    private int clickCount = 0; // Keep track of the number of clicks on the record button

    @Override
    public void onClick(View v) {
        if (v == recordButton) {
            clickCount++; // Increment the click count every time the record button is clicked

            if (clickCount == 1) {
                // First click: Start recording
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestRecordAudioPermission();
                } else {
                    editRecord.setVisibility(View.GONE);
                    startRecording();
                    recordButton.setImageResource(R.drawable.stop_icon);
                }
            } else if (clickCount == 2) {
                // Second click: Stop recording
                if (isRecording) {
                    stopRecording();
                    editRecord.setVisibility(View.VISIBLE);
                    recordButton.setImageResource(R.drawable.icon_send);
                } else {
                    // If recording is not in progress, reset click count and icon
                    clickCount = 0;
                    recordButton.setImageResource(R.drawable.ic_mic);
                    // Handle this case as needed (optional)
                }
            } else if (clickCount == 3) {
                // Third click: Send the record
                if (audioFile != null) {
                    // If there is a recorded file, upload it
                    uploadAudioToFirebase();
                    messageEt.setText("");
                    editRecord.setVisibility(View.GONE);
                    // Reset click count and icon for the next cycle
                    clickCount = 0;
                    recordButton.setImageResource(R.drawable.ic_mic);
                }
            }
        }
    }


    private void initUI() {


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageEt.setText("");
                editRecord.setVisibility(View.GONE);
                if (isRecording) {
                    stopRecording(); // Stop recording if in progress
                }
                clickCount = 0;

                deleteRecording(); // Delete the recording
                recordButton.setImageResource(R.drawable.ic_mic);


            }
        });

        listenAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle listen again action
                if (!isPlaying) {
                    playAudioFromUrl(audioFile);
                    listenAgainButton.setImageResource(R.drawable.stop_icon); // Change button image to pause icon
                    isPlaying = true;
                } else {
                    stopAudioFromUrl();
                    listenAgainButton.setImageResource(R.drawable.play_icon); // Change button image to play icon
                    isPlaying = false;
                }
            }
        });

    }

    private String formatTime(long seconds) {
        int minutes = (int) (seconds / 60);
        int remainingSeconds = (int) (seconds % 60);
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private void saveRecordsInFirebase(String audioUrl) {

        LocalDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        // Format the current date and time using the defined formatter
        String formattedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDateTime = currentDateTime.format(formatter);
        }

        String AudioName, RecordId, RecordTime, url, SenderId, message;
        AudioName = generateUniqueFileName();
        RecordId = generateRecordId();
        RecordTime = formattedDateTime;
        url = audioUrl;
        SenderId = userId;
        message = messageEt.getText().toString();
        group_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    if (group.getGroupId().equals(groupId) && group.getManagerId().equals(SenderId)) {
                        Record newRecord = new Record(AudioName, RecordId, RecordTime, audioUrl, SenderId, groupId, message);
                        for (String uid : group.getMembers()) {
                            newRecord.send_to_user(uid);
                        }
                        submitRecord(newRecord,group.getGroupName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void submitRecord(Record newRecord, String groupName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Records").push();
        String Rid = reference.getKey();
        newRecord.setRecordId(Rid);
        reference.setValue(newRecord).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AudioManagerRecordingActivity.this, "Record sent to " + groupName + " successfully!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(AudioManagerRecordingActivity.this, "Failed sending the record!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Recording permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            audioFile = getOutputFilePath();
            mediaRecorder.setOutputFile(audioFile);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Recording failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getOutputFilePath() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        return storageDir.getAbsolutePath() + generateRecordId();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRecording();
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            Toast.makeText(this, "Recording stopped!", Toast.LENGTH_SHORT).show();


            // Upload the recorded audio file to Firebase Storage
        }
    }


        private void uploadAudioToFirebase() {
        if (audioFile != null) {
            Uri fileUri = Uri.fromFile(new File(audioFile));
            StorageReference audioRef = storageReference.child("audio").child( System.currentTimeMillis() + ".mp3");
            audioRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // uri is the download URL of the uploaded audio file
                            audioUrl = uri.toString();
                            // Now you can use audioUrl as needed
                            Toast.makeText(AudioManagerRecordingActivity.this, "Recording sent to Firebase", Toast.LENGTH_SHORT).show();

                            // You can pass the audioUrl to any method or store it for later use
                            // Example: saveAudioUrl(audioUrl);

                            // Save the record in Firebase after getting the audioUrl
                            saveRecordsInFirebase(audioUrl);
                        });




                        Toast.makeText(AudioManagerRecordingActivity.this, "Audio uploaded to Firebase Storage!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AudioManagerRecordingActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String generateUniqueFileName() {
        // Implement your logic to generate a unique file name
        // You might want to use timestamps, UUIDs, or some other strategy
        return System.currentTimeMillis() + ".mp3";
    }

    public static String generateRecordId() {
        // Generate a random UUID
        UUID uuid = UUID.randomUUID();

        // Convert the UUID to a string and remove dashes
        return uuid.toString().replace("-", "");
    }
    private void deleteRecording() {
        if (audioFile != null) {
            File fileToDelete = new File(audioFile);
            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    Toast.makeText(this, "Recording deleted successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, you can reset UI elements related to recording here
                    audioFile = null; // Reset audio file path
                } else {
                    Toast.makeText(this, "Failed to delete recording", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "No recording to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudioFromUrl(String url) {
        if (audioFile != null) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    // Optionally, you can add controls to pause/stop the playback or handle playback completion
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to play recording", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!mediaPlayer.isPlaying()) {
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to resume playback", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            Toast.makeText(this, "No recording to play", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAudioFromUrl() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }




}




