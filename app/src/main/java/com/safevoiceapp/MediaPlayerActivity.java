package com.safevoiceapp;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.safevoiceapp.R;

import java.io.IOException;

public class MediaPlayerActivity extends AppCompatActivity {
    private String mp3Url;
    private DatabaseReference record_reference;
    private MediaPlayer mediaPlayer;
    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        record_reference = FirebaseDatabase.getInstance().getReference("Records");

        playButton = findViewById(R.id.playButton);

        mediaPlayer = new MediaPlayer();

        // Set the audio attributes for the media player (for API level 21 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAudioAttributes();
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace "YOUR_MP3_URL" with the actual URL of your MP3 file
                playAudioFromUrl("https://firebasestorage.googleapis.com/v0/b/safevoice-2b6b5.appspot.com/o/audio%2Fdym_down%20(1).mp3?alt=media&token=afacba61-81cf-4a4f-b0dd-90f6bf0db4f1");
            }
        });
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
}
