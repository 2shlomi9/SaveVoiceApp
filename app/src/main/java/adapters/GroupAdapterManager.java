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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.safevoiceapp.AddGroupActivity;
import com.safevoiceapp.AudioRecordingActivity;
import com.safevoiceapp.MainActivity;
import com.safevoiceapp.R;
import com.safevoiceapp.loginActivity;

import java.util.ArrayList;
import java.util.List;

import classes.Group;
import classes.Record;
import classes.User;

public class GroupAdapterManager extends RecyclerView.Adapter<GroupAdapterManager.MyViewHolder> {

    Context context;
    ArrayList<Group> list;

//    private FirebaseStorage storage;
//    private StorageReference storageReference;
    public static String currUid;
    private AlertDialog.Builder groupOptions;
    private AlertDialog options;
    private DatabaseReference group_reference, user_reference;
    Bitmap bitmap;
    private List<String> group_manager;
    String participantstr = "";






    public GroupAdapterManager(Context context, ArrayList<Group> list, AlertDialog.Builder groupOptions) {
        this.context = context;
        this.list = list;
        this.user_reference = FirebaseDatabase.getInstance().getReference("Users");
        this.group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        this.currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.groupOptions = groupOptions;
        this.group_manager = new ArrayList<String>();

        //this.options = options;

    }

    @NonNull
    @Override
    public GroupAdapterManager.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.group_view, parent, false);
        return new GroupAdapterManager.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GroupAdapterManager.MyViewHolder holder, int position) {
        Group group = list.get(position);
        holder.title.setText(group.getGroupName());

        DatabaseReference ureference = FirebaseDatabase.getInstance().getReference("Users");
        final String[] Rid = new String[1];
       // Set group button
        holder.group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , AudioRecordingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Group_ID", group.getGroupId());
                intent.putExtra("Group_NAME", group.getGroupName());
                context.startActivity(intent);


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

        CardView group_btn;
        TextView title;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            group_btn = itemView.findViewById(R.id.group_view);
            title = itemView.findViewById(R.id.tvTitle);



        }
    }
}