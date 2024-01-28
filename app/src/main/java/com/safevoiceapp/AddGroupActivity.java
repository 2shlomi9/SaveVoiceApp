package com.safevoiceapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AddGroupActivity extends AppCompatActivity {

    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        btnAdd = findViewById(R.id.btnCreateGroup);
        initUI();  // Call initUI here after addGroup is initialized
    }

    private void initUI() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddGroupActivity.this,
                        AddGroupIncideActivity.class);
                startActivity(intent);
            }

        });
    }


}
