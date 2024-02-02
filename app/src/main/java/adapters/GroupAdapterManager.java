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
                currUid=group.getGroupId();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View contactPopupView = inflater.inflate(R.layout.group_options_manager, null);

                TextView title;
                Spinner members;
                Button editBtn,deleteBtn, inviteBtn;
                EditText userInviteTxt;
                title = contactPopupView.findViewById(R.id.tvTitle);
                deleteBtn = contactPopupView.findViewById(R.id.deleteGroup);
                inviteBtn = contactPopupView.findViewById(R.id.Invite);
                editBtn = contactPopupView.findViewById(R.id.editGroup);
                members = contactPopupView.findViewById(R.id.tvMembers);
                userInviteTxt = contactPopupView.findViewById(R.id.etUserName);

                   //set title
                title.setText(group.getGroupName());

//                //Set participants
                DatabaseReference referenceD = FirebaseDatabase.getInstance().getReference("Groups").child(group.getGroupId());
                referenceD.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Group g = snapshot.getValue(Group.class);
                        if (g != null) {
                            if (g.getMembers().isEmpty()) {
                                String[] participants = {"no participant"};
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, participants);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                members.setAdapter(adapter);
                            } else {
                                ArrayList<String> participants = new ArrayList<String>();
                                for (int i = 0; i < g.getMembers().size(); i++) {
                                    ureference.child(g.getMembers().get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User profile = snapshot.getValue(User.class);
                                            if (profile != null) {
                                                participants.add(profile.getFullName());
                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, participants);
                                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                members.setAdapter(adapter);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled (@NonNull DatabaseError error){

                    }
                });




                groupOptions.setView(contactPopupView);
                options = groupOptions.create();
                options.show();


                //Set invite button
                inviteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userName = userInviteTxt.getText().toString();
                        user_reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userId="";
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    User user = dataSnapshot.getValue(User.class);
                                    assert user != null;
                                    if (user.getuserName().equals(userName)) {
                                        userId = user.getUid();
                                    }
                                }
                                if(group.getMembers().contains(userId)){
                                    Toast.makeText(context, userName+" already member in "+group.getGroupName(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else if(userId.equals("")){
                                    Toast.makeText(context, userName+" is not exists.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else{
                                    group.addMembers(userId);
                                    group_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            group_reference.child(group.getGroupId()).setValue(group);
                                            Toast.makeText(context.getApplicationContext(), "The invitation sent to "+userName + " successfully!", Toast.LENGTH_SHORT).show();
                                            userInviteTxt.setText("");
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            System.out.println("Failed.");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                //Set edit button
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent editIntent = new Intent(context.getApplicationContext(), AddGroupActivity.class);
                        context.startActivity(editIntent);
                        options.cancel();
                    }
                });

                //Set delete button
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Group.deleteGroupById(group.getGroupId());
                        options.cancel();
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

        CardView group_btn;
        TextView title;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            group_btn = itemView.findViewById(R.id.group_view);
            title = itemView.findViewById(R.id.tvTitle);



        }
    }
}