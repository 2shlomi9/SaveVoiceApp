package com.safevoiceapp;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import classes.Record;

public class SafeModeActivity extends AppCompatActivity {
    private String mp3Url;
    private MediaPlayer mediaPlayer;
    private Button playButton;
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
    private String group_text, groupIdToSend, Uid, enterDate, alarmUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_mode);

        // Detect when the app is launched and pin the screen
        startLockTask();
        alarmUrl = "https://firebasestorage.googleapis.com/v0/b/safevoice-2b6b5.appspot.com/o/audio%2FAlarm.mp3?alt=media&token=af43c1c5-9928-43ac-a56d-07b374dcdf37";




        LocalDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        enterDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            enterDate = currentDateTime.format(formatter);
        }
        record_reference = FirebaseDatabase.getInstance().getReference("Records");
        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        mediaPlayer = new MediaPlayer();

        // Set the audio attributes for the media player (for API level 21 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAudioAttributes();
        }



        record_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                startBurstRecording(snapshot);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void startBurstRecording(DataSnapshot snapshot){
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Record record = dataSnapshot.getValue(Record.class);
            if (record.is_sent_to_user(Uid) &&isDateAfter(record.getRecordTime(), enterDate)) {
                playSequentially(alarmUrl,record.getUrl());

                record.deliver_to_user(Uid);
                record_reference.child(record.getRecordId()).setValue(record);
            }
        }

    }


    // Set audio attributes for the media player (for API level 21 and above)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setAudioAttributes() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        mediaPlayer.setAudioAttributes(audioAttributes);
    }
    public static boolean isDateAfter(String recDate,String enterDate) {
        // Parse the string date into a Date object
        Date firstDate = null;
        Date secondDate = null;

        try {
            firstDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(recDate);
            secondDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(enterDate);
        } catch (ParseException e) {
            System.err.println("Error parsing date string: " + e.getMessage());
            return false;
        }
        if (firstDate.before(secondDate))
            return false;
        else
            return true;
    }

    // Play audio from the given URL
    private void playAudioFromUrl(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
    private void playSequentially(String firstUrl, String secondUrl) {
        // Set completion listener to play the second URL after the first one finishes
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Reset the listener to avoid being called for future playback
                mediaPlayer.setOnCompletionListener(null);

                // Play the second URL
                playAudioFromUrl(secondUrl);
            }
        });

        // Play the first URL
        playAudioFromUrl(firstUrl);
    }

}
