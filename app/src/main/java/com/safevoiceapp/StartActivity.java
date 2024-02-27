package com.safevoiceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class StartActivity extends AppCompatActivity {

    private VideoView videoView;
    private LinearLayout linearLayout;
    private Button register;
    private Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Example for Realtime Database
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true); // Enable offline persistence if needed

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        videoView = findViewById(R.id.video_view);
        linearLayout = findViewById(R.id.linear_layout);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);


        linearLayout.animate().alpha(0f).setDuration(10);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.start2; // Replace with your video file
        // Set the video URI
        Uri videoUri = Uri.parse(videoPath);

        // Set the video URI to the VideoView
        videoView.setVideoURI(videoUri);

        // Start playing the video
        videoView.start();

        // Set a completion listener to handle actions after video playback completes
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Video playback has completed
                videoView.animate().alpha(0f).setDuration(1000).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Make the VideoView invisible after fading out
                        videoView.setVisibility(View.INVISIBLE);
                        // Fade in the linearLayout
                        linearLayout.animate().alpha(1f).setDuration(1000);
                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this , registerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this , loginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(StartActivity.this , MainActivity.class));
            finish();
        }
    }
}