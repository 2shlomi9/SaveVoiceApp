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

public class GroupAdapterMember extends RecyclerView.Adapter<GroupAdapterMember.MyViewHolder> {

    Context context;
    ArrayList<Group> list;

    //    private FirebaseStorage storage;
//    private StorageReference storageReference;
    public static String currUid, Uid;
    private AlertDialog.Builder groupOptions;
    private AlertDialog options;
    private DatabaseReference group_reference, user_reference;
    Bitmap bitmap;
    private List<String> group_members;
    String participantstr = "";






    public GroupAdapterMember(Context context, ArrayList<Group> list, AlertDialog.Builder groupOptions) {
        this.context = context;
        this.list = list;
        this.user_reference = FirebaseDatabase.getInstance().getReference("Users");
        this.group_reference = FirebaseDatabase.getInstance().getReference("Groups");
        this.Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.groupOptions = groupOptions;
        this.group_members = new ArrayList<String>();

        //this.options = options;

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

        DatabaseReference ureference = FirebaseDatabase.getInstance().getReference("Users");
        final String[] Rid = new String[1];
        // Set group button
        holder.group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currUid=group.getGroupId();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View contactPopupView = inflater.inflate(R.layout.group_options_member, null);

                TextView title, managertxt;
                Spinner members;
                Button exitBtn;
                title = contactPopupView.findViewById(R.id.tvTitle);
                exitBtn = contactPopupView.findViewById(R.id.exitGroup);
                members = contactPopupView.findViewById(R.id.tvMembers);
                managertxt = contactPopupView.findViewById(R.id.managerIdTv);

                //set title
                title.setText(group.getGroupName());

                //set manager name
                user_reference.child(group.getManagerId()).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User manager = snapshot.getValue(User.class);
                        if (manager != null){
                            managertxt.setText("Group Manager: \n"+manager.getuserName()+" ("+manager.getFullName()+")");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Set participants
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


                //Set exit button
                exitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        group.remove_member(Uid);
                        group_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                group_reference.child(group.getGroupId()).setValue(group);
                                Toast.makeText(context.getApplicationContext(), "exited from group "+group.getGroupName() + " successfully!", Toast.LENGTH_SHORT).show();
                                options.cancel();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                System.out.println("Failed.");
                            }
                        });

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

        AlertDialog.Builder dinnerOptions;
        AlertDialog options;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            group_btn = itemView.findViewById(R.id.group_view);
            title = itemView.findViewById(R.id.tvTitle);



        }
    }
}