package com.safevoiceapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import classes.Group;
import classes.Record;


public class RecordingActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1001;
    private String generatedDocumentId,userId,audioUrl;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;

    private ImageView recordButton;
    private TextView recordingDuration;
    private Button deleteButton;
    private Button listenAgainButton;
    private Button sendButton;

    private boolean isRecording = false;
    private CountDownTimer countDownTimer;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference user_reference, group_reference, record_reference;
    private CollectionReference collectionRef;
    private Spinner group_select;
    private ArrayList<String> managerGroups_id,managerGroups_names ;
    private String group_text,groupIdToSend, Uid;

    private List<String> geters;
    private Record_handle recordHandle;





    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        managerGroups_id = new ArrayList<String>();
        managerGroups_names = new ArrayList<String>();
        recordHandle = new Record_handle(); // Assuming Group_handle has a default constructor
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        record_reference = FirebaseDatabase.getInstance().getReference("Records");
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();


        recordButton = findViewById(R.id.btnRecord);
        recordingDuration = findViewById(R.id.tvRecordingDuration);
        deleteButton = findViewById(R.id.btnDelete);
        listenAgainButton = findViewById(R.id.btnListenAgain);
        sendButton = findViewById(R.id.btnSend);
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
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
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

        // Check and request runtime permissions
        checkMicrophonePermissionAndInitUI();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        geters = new ArrayList<>();
        initUI();
    }

    private void refresh_menagerGroupsData(DataSnapshot snapshot) {
        managerGroups_names.clear();
        managerGroups_id.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Group group = dataSnapshot.getValue(Group.class);
            if(group.getManagerId().equals(Uid)) {
                managerGroups_id.add(group.getGroupId());
                managerGroups_names.add(group.getGroupName());
            }
        }


    }
    private void initUI() {
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecording) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecording();
            }
        });

        listenAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle listen again action
                Toast.makeText(RecordingActivity.this, "Listening again", Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAudioToFirebase();
            }
        });
    }

    private void checkMicrophonePermissionAndInitUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_PERMISSION_CODE);
            } else {
                // Permission already granted, initialize UI
                initUI();
            }
        } else {
            // For devices below Android M, no runtime permission needed, initialize UI
            initUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize UI
                initUI();
            } else {
                // Permission denied, show a message or redirect to app settings
                Toast.makeText(this, "Microphone permission is required", Toast.LENGTH_SHORT).show();
                finish(); // Optionally, finish the activity or take appropriate action
            }
        }
    }

    private void startRecording() {
        // Your start recording logic goes here
        // Make sure to handle the recording logic based on your requirements

        isRecording = true;
        recordButton.setImageResource(R.drawable.ic_stop); // Change the icon to stop

        // Initialize MediaRecorder
        setupMediaRecorder();

        // Start recording duration countdown
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the recording duration
                recordingDuration.setText(formatTime(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Handle when recording duration reaches maximum
            }
        }.start();

        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        // Your stop recording logic goes here
        // Make sure to handle the stop recording logic based on your requirements

        isRecording = false;
        recordButton.setImageResource(R.drawable.ic_record); // Change the icon back to record

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        recordingDuration.setText("00:00"); // Reset recording duration

        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
            } catch (RuntimeException stopException) {
                // Handle runtime exception while stopping
            }
        }

        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
    }

    private void deleteRecording() {
        stopRecording();

        // Delete the local recording file
        File audioFile = new File(audioFilePath);
        if (audioFile.exists()) {
            audioFile.delete();
            Toast.makeText(this, "Recording deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No recording available to delete", Toast.LENGTH_SHORT).show();
        }

        resetUIForRecording(); // Added method to reset UI for recording
    }

    private void resetUIForRecording() {
        // Reset UI elements for recording
        recordButton.setImageResource(R.drawable.ic_record);
        recordingDuration.setText("00:00");

        // Reinitialize the MediaRecorder
        setupMediaRecorder();

        // Reset recording flag
        isRecording = false;
    }

    private String formatTime(long seconds) {
        int minutes = (int) (seconds / 60);
        int remainingSeconds = (int) (seconds % 60);
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private void setupMediaRecorder() {
        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/audio.3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAudioToFirebase() {
        if (audioFilePath != null) {
            // Create a reference to the audio file in Firebase Storage
            StorageReference audioRef = storageRef.child("audio/" + System.currentTimeMillis() + ".mp3");

            // Create Uri for the local audio file
            Uri audioFileUri = Uri.fromFile(new File(audioFilePath));

            // Upload file to Firebase Storage
            audioRef.putFile(audioFileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // File successfully uploaded
                        // Get the download URL of the uploaded file
                        audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // uri is the download URL of the uploaded audio file
                            audioUrl = uri.toString();
                            // Now you can use audioUrl as needed
                            Toast.makeText(RecordingActivity.this, "Recording sent to Firebase", Toast.LENGTH_SHORT).show();

                            // You can pass the audioUrl to any method or store it for later use
                            // Example: saveAudioUrl(audioUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure
                        Toast.makeText(RecordingActivity.this, "Failed to send recording: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No recording available to send", Toast.LENGTH_SHORT).show();
        }
        saveRecordsInFirebase(audioUrl);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopRecording();
        }
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
        group_reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean flag = true;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    if(group.getGroupName().equals(group_text)) {
                        flag =false;
                        String groupId = group.getGroupId();
                        Record newRecord = new Record(AudioName, RecordId, RecordTime, url, SenderId, groupId);
                        submitRecord(newRecord);
                    }
                }
                if (flag)
                    Toast.makeText(RecordingActivity.this, "Please select a group.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void submitRecord(Record newRecord) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Records").push();
        String Rid=reference.getKey();
        newRecord.setRecordId(Rid);
        reference.setValue(newRecord).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(RecordingActivity.this,  "Record sent to "+group_text+" successfully!", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(RecordingActivity.this, "Failed sending the record!", Toast.LENGTH_SHORT).show();
            }
        });



    }


    private String generateUniqueFileName() {
        // Implement your logic to generate a unique file name
        // You might want to use timestamps, UUIDs, or some other strategy
        return "audio_" + System.currentTimeMillis() + ".mp3";
    }
    public static String generateRecordId() {
        // Generate a random UUID
        UUID uuid = UUID.randomUUID();

        // Convert the UUID to a string and remove dashes
        return uuid.toString().replace("-", "");
    }

}
