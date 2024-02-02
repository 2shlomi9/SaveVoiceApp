package com.safevoiceapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import classes.Group;
import android.net.Uri;
import classes.Record;

public class AudioRecordingActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {


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

    private ImageView recordButton;
    private TextView recordingDuration;

    private Button deleteButton;
    private Button listenAgainButton;
    private Button sendButton;

    //private boolean isRecording = false;
    private CountDownTimer countDownTimer;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference user_reference, group_reference, record_reference;
    private Spinner group_select;
    private ArrayList<String> managerGroups_id, managerGroups_names;
    private String group_text, groupIdToSend, Uid;

    private List<String> geters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recording);

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        recordButton = findViewById(R.id.btnRecord);
        recordButton.setOnClickListener(this);
        recordingDuration = findViewById(R.id.tvRecordingDuration);
        deleteButton = findViewById(R.id.btnDelete);
        listenAgainButton = findViewById(R.id.btnListenAgain);
        sendButton = findViewById(R.id.btnSend);
        managerGroups_id = new ArrayList<String>();
        managerGroups_names = new ArrayList<String>();
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        record_reference = FirebaseDatabase.getInstance().getReference("Records");
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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


        //group filter
        group_select = findViewById(R.id.group_select);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, managerGroups_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        group_select.setAdapter(adapter);
        group_text = "a";
        group_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group_text = parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                deleteButton.setText(group_text);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
    @Override
    public void onClick(View v) {
        if (v == recordButton) {
            if (!isRecording) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestRecordAudioPermission();
                } else {
                    startRecording();
                }
            } else if (isRecording) {
                stopRecording();
            }
        }
    }

    private void initUI() {


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deleteRecording();
            }
        });

        listenAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle listen again action
                Toast.makeText(AudioRecordingActivity.this, "Listening again", Toast.LENGTH_SHORT).show();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAudioToFirebase();
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

        String AudioName, RecordId, RecordTime, url, SenderId;
        AudioName = generateUniqueFileName();
        RecordId = generateRecordId();
        RecordTime = formattedDateTime;
        url = audioUrl;
        SenderId = userId;
        group_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean flag = true;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    if (group.getGroupName().equals(group_text) && group.getManagerId().equals(SenderId)) {
                        flag = false;
                        String groupId = group.getGroupId();
                        Record newRecord = new Record(AudioName, RecordId, RecordTime, audioUrl, SenderId, groupId);
                        for (String uid : group.getMembers()) {
                            newRecord.send_to_user(uid);
                        }
                        submitRecord(newRecord);
                    }
                }
                if (flag)
                    Toast.makeText(AudioRecordingActivity.this, "Please select a group.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void submitRecord(Record newRecord) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Records").push();
        String Rid = reference.getKey();
        newRecord.setRecordId(Rid);
        reference.setValue(newRecord).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AudioRecordingActivity.this, "Record sent to " + group_text + " successfully!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(AudioRecordingActivity.this, "Failed sending the record!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AudioRecordingActivity.this, "Recording sent to Firebase", Toast.LENGTH_SHORT).show();

                            // You can pass the audioUrl to any method or store it for later use
                            // Example: saveAudioUrl(audioUrl);

                            // Save the record in Firebase after getting the audioUrl
                            saveRecordsInFirebase(audioUrl);
                        });




                        Toast.makeText(AudioRecordingActivity.this, "Audio uploaded to Firebase Storage!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AudioRecordingActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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



}




