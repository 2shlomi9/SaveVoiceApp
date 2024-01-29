package com.safevoiceapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classes.User;


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
    private CollectionReference collectionRef;

    private List<String> geters;




    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        recordButton = findViewById(R.id.btnRecord);
        recordingDuration = findViewById(R.id.tvRecordingDuration);
        deleteButton = findViewById(R.id.btnDelete);
        listenAgainButton = findViewById(R.id.btnListenAgain);
        sendButton = findViewById(R.id.btnSend);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, get their UID
            userId = currentUser.getUid();
        }

        // Check and request runtime permissions
        checkMicrophonePermissionAndInitUI();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        geters = new ArrayList<>();
        collectionRef = db.collection("Records");
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


        Map<String, Object> RecordsData = new HashMap<>();

        RecordsData.put("nameAudio", generateUniqueFileName());
        RecordsData.put("recordTime", formattedDateTime);
        RecordsData.put("FromGroupId", "anthing");
        RecordsData.put("MengerId", userId);
        RecordsData.put("getrsId", geters);
        RecordsData.put("url", audioUrl);




        collectionRef.add(RecordsData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Document added with generated ID
                        generatedDocumentId = documentReference.getId();
                        Log.d("Firestore", "Document added with ID: " + generatedDocumentId);

                        // Now, add the generated ID to your user data HashMap
                        RecordsData.put("RecordId", generatedDocumentId);

                        // Update the document with the firestoreId
                        documentReference.update("RecordId", generatedDocumentId)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Firestore", "RecordId updated successfully");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Firestore", "Error updating RecordId", e);
                                    }
                                });

                        // Print the userData with the Firestore-generated ID
                        Log.d("Firestore", "User data with Firestore ID: " + RecordsData.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error adding document", e);
                    }
                });

    }
    private String generateUniqueFileName() {
        // Implement your logic to generate a unique file name
        // You might want to use timestamps, UUIDs, or some other strategy
        return "audio_" + System.currentTimeMillis() + ".mp3";
    }

}
