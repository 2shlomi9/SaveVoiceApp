package com.safevoiceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                    return true;
                } else if (itemId == R.id.nav_group) {
                    startActivity(new Intent(MainActivity.this, ManagerGroupActivity.class));
                    return true;
                } else if (itemId == R.id.nav_add) {// Start the activity outside of the switch statement
                    startActivity(new Intent(MainActivity.this, AudioRecordingActivity.class));
                    return true; // Return true to prevent further processing
                } else if (itemId == R.id.voice_chat) {
                    startActivity(new Intent(MainActivity.this, MemberGroupActivity.class));
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true; // Return true to prevent further processing
                }



                return true;
            }
        });


    }
}