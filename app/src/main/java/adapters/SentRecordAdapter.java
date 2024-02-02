package adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.safevoiceapp.AddGroupActivity;
import com.safevoiceapp.R;

import java.util.ArrayList;
import java.util.List;

import classes.Group;
import classes.User;
import classes.Record;

public class SentRecordAdapter extends RecyclerView.Adapter<SentRecordAdapter.MyViewHolder> {

    Context context;
    ArrayList<Record> list;
    private AlertDialog.Builder recordOptions;
    private AlertDialog options;
    private ImageView playButton;

    //    private FirebaseStorage storage;
//    private StorageReference storageReference;
    public static String currUid;

    private DatabaseReference group_reference, user_reference , records_reference;
    Bitmap bitmap;
    private List<String> sent_records;







    public SentRecordAdapter(Context context, ArrayList<Record> list, AlertDialog.Builder recordOptions) {
        this.context = context;
        this.list = list;
        this.records_reference = FirebaseDatabase.getInstance().getReference("Records");
        this.currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.sent_records = new ArrayList<String>();
        this.recordOptions = recordOptions;
     //   this.sent_records.add( new Record("asasdad","asdas","asdasd","asdd","asdas","asd"));

        this.options = options;

    }


    @NonNull
    @Override
    public SentRecordAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.record_view, parent, false);
        return new SentRecordAdapter.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SentRecordAdapter.MyViewHolder holder, int position) {
        Record record = list.get(position);
        holder.title.setText(record.getRecordId());

        DatabaseReference record_reference = FirebaseDatabase.getInstance().getReference("Records");
        final String[] Rid = new String[1];
        // Set group button
//        holder.record_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                currUid=record.getRecordId();
//                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View contactPopupView = inflater.inflate(R.layout.record_player, null);
//
//
//                TextView title;
//                playButton.findViewById(R.id.btnplay);
////                title = contactPopupView.findViewById(R.id.tvTitle);
////                title.setText(record.getAudioName());
////
//                playButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        recordOptions.setView(contactPopupView);
//                        options = recordOptions.create();
//                        options.show();
//                    }
//                });
//
//            };
//
//        });

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


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            record_btn = itemView.findViewById(R.id.record_view);
            title = itemView.findViewById(R.id.tvTitle);


        }
    }

}








//

//



