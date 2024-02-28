package com.safevoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import adapters.UserParticipantsAdapter;
import classes.Group;
import classes.User;
import classes.Record;

public class ReadConfigActivity extends AppCompatActivity {
    private String senderId, recordId,groupId;
    private ImageView btnplay;
    private Context context;
    private DatabaseReference group_reference, user_reference,record_reference;

    private Group group;

    private String audioUrl = null;
    private MediaPlayer mediaPlayer;
    private UserParticipantsAdapter user_participants_adapter;
    private String Uid;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialog_builder;
    private ArrayList<User> user_participants;
    private TextView txtTitle;
    private Spinner members;
    private ImageView exitBtn, backBtn;
    private Button viewBtn;
    private EditText userInviteTxt;
    private boolean isPlaying = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_config);

        Intent intent = getIntent();
        recordId = intent.getStringExtra("RecordID");
        senderId = intent.getStringExtra("RecordSender");
        groupId = intent.getStringExtra("GroupID");
        audioUrl = intent.getStringExtra("URL");
        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user_participants = new ArrayList<User>();
        //set participants adapter
        recyclerView = findViewById(R.id.participants_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialog_builder = new AlertDialog.Builder(this);
        user_participants_adapter = new UserParticipantsAdapter(this, groupId, user_participants, dialog_builder);
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        record_reference =FirebaseDatabase.getInstance().getReference("Records");
        txtTitle = findViewById(R.id.txtTitle);
        viewBtn = findViewById(R.id.viewBtn);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewBtn.setVisibility(View.GONE);
                txtTitle.setVisibility(View.VISIBLE);
                refresh_users();
            }
        });
    }

private void refresh_users() {
    user_reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            user_participants.clear();

            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                User user = userSnapshot.getValue(User.class);

                record_reference.child(recordId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Record record = dataSnapshot.getValue(Record.class);

                        if (record.getDelivered_users().contains(user.getUid()) && user.getUid() != Uid && record.getSenderId() != user.getUid()) {
                            user_participants.add(user);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
            }
            recyclerView.setAdapter(user_participants_adapter);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            // Handle onCancelled
        }
    });
}

    private void playAudioFromUrl(String url) {
        if (audioUrl != null) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioUrl);
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