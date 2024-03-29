package adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.safevoiceapp.AudioManagerRecordingActivity;
import com.safevoiceapp.AudioMemberRecordingActivity;
import com.safevoiceapp.R;

import java.util.ArrayList;
import java.util.List;

import classes.Group;

public class GroupAdapterMember extends RecyclerView.Adapter<GroupAdapterMember.MyViewHolder> {

    Context context;
    ArrayList<Group> list;

    public static String currUid;
    private AlertDialog.Builder groupOptions;
    private AlertDialog options;
    private DatabaseReference group_reference, user_reference;
    Bitmap bitmap;
    private List<String> group_member;
    String participantstr = "";






    public GroupAdapterMember(Context context, ArrayList<Group> list, AlertDialog.Builder groupOptions) {
        this.context = context;
        this.list = list;
        this.user_reference = FirebaseDatabase.getInstance().getReference("Users");
        this.group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        this.currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.groupOptions = groupOptions;
        this.group_member = new ArrayList<String>();


    }

    @NonNull
    @Override
    public GroupAdapterMember.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.group_view, parent, false);
        return new GroupAdapterMember.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GroupAdapterMember.MyViewHolder holder, int position) {
        Group group = list.get(position);
        holder.title.setText(group.getGroupName());

        DatabaseReference user_reference = FirebaseDatabase.getInstance().getReference("Users");
        final String[] Rid = new String[1];
        // Set group button
        holder.group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , AudioMemberRecordingActivity.class);
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