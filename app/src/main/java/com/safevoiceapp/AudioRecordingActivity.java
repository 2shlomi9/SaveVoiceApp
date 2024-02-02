package com.safevoiceapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    public class AudioRecordingActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

        private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
        private ImageView recordButton;
        private MediaRecorder mediaRecorder;
        private String audioFile = null;
        private boolean isRecording = false;
        private Button stopRecordingButton;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_audio_recording);

            recordButton = findViewById(R.id.btnRecord);
            recordButton.setOnClickListener(this);
            stopRecordingButton = findViewById(R.id.btnStopRecording);

        }



        @Override
        public void onClick(View v) {
            if (v == recordButton) {
                if (!isRecording){
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        requestRecordAudioPermission();
                    } else {
                        startRecording();
                    }
                }

            }
            if (v == stopRecordingButton){
                if (isRecording){
                    stopRecording();
                }
            }
        }

        private void requestRecordAudioPermission() {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                mediaRecorder.setOutputFile(getOutputFilePath());
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.prepare();
                mediaRecorder.start();
                Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Recording failed!", Toast.LENGTH_SHORT).show();
            }
        }

        private String getOutputFilePath() {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            return storageDir.getAbsolutePath() + "/recording.mp4";
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
                Toast.makeText(this, "Recording stopped!", Toast.LENGTH_SHORT).show();
            }
        }
    }
