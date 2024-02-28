package adapters;


import android.annotation.SuppressLint;
import androidx.annotation.RequiresApi;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.safevoiceapp.R;
import com.safevoiceapp.ReadConfigActivity;

import java.io.IOException;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import classes.Record;
import classes.User;

public class ReceiveRecordAdapter extends RecyclerView.Adapter<ReceiveRecordAdapter.MyViewHolder> {

    Context context;
    ArrayList<Record> list;
    String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private AlertDialog.Builder recordOptions;
    private MediaPlayer mediaPlayer;
    private boolean isplaying = false;
    private AlertDialog options;
    private ImageView playButton;

    private boolean isPlaying = false;
    public static String currUid;

    private DatabaseReference group_reference, user_reference , records_reference;
    Bitmap bitmap;
    private List<String> sent_records;








    public ReceiveRecordAdapter(Context context, ArrayList<Record> list, AlertDialog.Builder recordOptions) {
        this.context = context;
        this.list = list;
        this.records_reference = FirebaseDatabase.getInstance().getReference("Records");
        this.user_reference = FirebaseDatabase.getInstance().getReference("Users");
        this.currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.sent_records = new ArrayList<String>();
        this.recordOptions = recordOptions;

        this.options = options;
        mediaPlayer = new MediaPlayer();

        // Set the audio attributes for the media player (for API level 21 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAudioAttributes();
        }

    }


    @NonNull
    @Override
    public ReceiveRecordAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.record_view, parent, false);
        return new ReceiveRecordAdapter.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReceiveRecordAdapter.MyViewHolder holder, int position) {
        Record record = list.get(position);
        user_reference.child(record.getSenderId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.title.setText(user.getuserName() + "(" + user.getFullName() + ")"+ getCurrentDate());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if(record.is_delivered_to_user(Uid))
            holder.title.setBackgroundColor(R.color.newGray);
        DatabaseReference record_reference = FirebaseDatabase.getInstance().getReference("Records");
        final String[] Rid = new String[1];

        // Set record button
        holder.record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , ReadConfigActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("RecordID", record.getRecordId());
                intent.putExtra("RecordSender", record.getSenderId());
                intent.putExtra("GroupID",record.getGroupId());
                intent.putExtra("URL",record.getUrl());
                context.startActivity(intent);


            };

        });
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    playAudioFromUrl(record.getUrl());
                    holder.btnPlay.setImageResource(R.drawable.stop_icon);// Change button image to pause icon
                    isPlaying = true;
                } else {
                    stopAudioFromUrl();
                    holder.btnPlay.setImageResource(R.drawable.play_icon); // Change button image to play icon
                    isPlaying = false;
                }
                record_reference.child(record.getRecordId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Record record = dataSnapshot.getValue(Record.class);
                        if (record.getSenderId() != Uid) {
                            record.deliver_to_user(Uid);
                            record_reference.child(record.getRecordId()).setValue(record);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }

                });
            }
        });

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public void createDialog() {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView record_btn;
        TextView title;
        ImageView btnPlay;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPlay = itemView.findViewById(R.id.btnplay);
            record_btn = itemView.findViewById(R.id.record_view);
            title = itemView.findViewById(R.id.tvTitle);


        }
    }

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
    private void stopAudioFromUrl(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }
    // Add the getCurrentDate method
    private String getCurrentDate() {
        // Specify the date format you want
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get the current date
        Date currentDate = new Date(System.currentTimeMillis());

        // Format the date as a string
        return dateFormat.format(currentDate);
    }

}






