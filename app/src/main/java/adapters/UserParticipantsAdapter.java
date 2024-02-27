package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.safevoiceapp.R;

import java.util.ArrayList;
import java.util.List;

import classes.Group;
import classes.User;

public class UserParticipantsAdapter extends RecyclerView.Adapter<UserParticipantsAdapter.MyViewHolder> {

    Context context;
    private EditText userInviteTxt;

    ArrayList<User> list;
    private Button search_user;

    //    private FirebaseStorage storage;
//    private StorageReference storageReference;
    public static String currUid;
    private AlertDialog.Builder userOptions;
    private AlertDialog options;
    private DatabaseReference group_reference, user_reference, greference;
    private String username, groupID;
    Bitmap bitmap;
    private List<String> group_members;
    String participantstr = "";






    public UserParticipantsAdapter(Context context, String groupID,ArrayList<User> list, AlertDialog.Builder userOptions) {
        this.context = context;
        this.list = list;
        this.user_reference = FirebaseDatabase.getInstance().getReference("Users");
        this.group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        this.userOptions = userOptions;
        this.groupID = groupID;
        this.greference = FirebaseDatabase.getInstance().getReference("Groups");


    }

    @NonNull
    @Override
    public UserParticipantsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_view, parent, false);
        return new UserParticipantsAdapter.MyViewHolder(v);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserParticipantsAdapter.MyViewHolder holder, int position) {
        User user = list.get(position);
        holder.title.setText(user.getuserName() + "(" +user.getFullName()+")");

        DatabaseReference ureference = FirebaseDatabase.getInstance().getReference("Users");
        final String[] Rid = new String[1];
        // Set group button
        holder.user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        CardView user_btn;
        TextView title;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_btn = itemView.findViewById(R.id.user_view);
            title = itemView.findViewById(R.id.tvTitle);



        }
    }



}
