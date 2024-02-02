package com.safevoiceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapters.ReceiveRecordAdapter;
import adapters.SentRecordAdapter;
import classes.Record;
import classes.User;

public class HistoryActivity extends AppCompatActivity {

    private ImageView createGroup;
    private ArrayList<Record> sent_records, received_records;

    private DatabaseReference record_reference, user_reference;
    private FirebaseUser fuser;
    private String Uid;
    private SentRecordAdapter sent_record_adapter;
    private ReceiveRecordAdapter receive_record_adapter;
    private User user;
    // private User_handle user_handle = new User_handle();
    private TextView title;
    private RecyclerView recyclerView;
    private AlertDialog.Builder dialog_builder;
    private Button btn_switch;
    private Boolean receive_mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        btn_switch =  findViewById(R.id.switchbtn);
        title =  findViewById(R.id.title_txt);
        receive_mode = true;
        received_records = new ArrayList<Record>();
        sent_records = new ArrayList<Record>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Uid = fuser.getUid();
        user_reference = FirebaseDatabase.getInstance().getReference("Users");
        record_reference = FirebaseDatabase.getInstance().getReference("Records");
        recyclerView = findViewById(R.id.recordsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog_builder = new AlertDialog.Builder(this);
        sent_record_adapter = new SentRecordAdapter(this, sent_records, dialog_builder);
        receive_record_adapter = new ReceiveRecordAdapter(this, received_records, dialog_builder);

        initUI();  // Call initUI here after addGroup is initialized

    }
    private void refresh_sentRecord(DataSnapshot snapshot) {
        sent_records.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Record record = dataSnapshot.getValue(Record.class);
            if(record.getSenderId().equals(Uid)) {
                sent_records.add(record);
            }
        }
    }
    private void refresh_receiveRecord(DataSnapshot snapshot) {
        received_records.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Record record = dataSnapshot.getValue(Record.class);
            if(record.is_delivered_to_user(Uid)||record.is_sent_to_user(Uid)) {
                received_records.add(record);
            }
        }
    }
    private void initUI(){

        record_reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                refresh_sentRecord(snapshot);
                refresh_receiveRecord(snapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                set_mode();

            }
        });
        set_mode();
        title.setText("History");
        btn_switch.setText("View Records");



    }
    private void set_mode(){
        if(receive_mode) {
            recyclerView.setAdapter(receive_record_adapter);
            btn_switch.setText("Sent Records");
            title.setText("Receive Records History");
        }
        else{
            recyclerView.setAdapter(sent_record_adapter);
            btn_switch.setText("Receive Records");
            title.setText("Sent Records History");
        }
        receive_mode = !receive_mode;
    }

}