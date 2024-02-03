package classes;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Group {
    private String groupName, groupId, managerId, description;
    private ArrayList<String> members;


    public Group(String groupName, String groupId, String managerId, String description,ArrayList<String> members) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.managerId = managerId;
        this.members = members;
        this.description = description;
    }
    public Group(String groupName, String groupId, String managerId, String description) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.managerId = managerId;
        this.description = description;
        this.members = new ArrayList<String>();
    }
    public Group() {
        this.members = new ArrayList<String>();
    }


    public static void deleteGroupById(String groupId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group group = dataSnapshot.getValue(Group.class);
                    if (group.getGroupId().equals(groupId)) {
                        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).removeValue();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }});
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManagerId() {
        return this.managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
    public void addMembers(String joinMember){
        this.members.add(joinMember);
    }
    public void remove_member(String deleteMember){
        this.members.remove(deleteMember);
    }

    public ArrayList<String> getMembers() {
        return this.members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public boolean IsExistInMember(String idMember){
        if (this.members.contains(idMember)){
            return true;
        }else{
            return false;
        }
    }
    public String toString(){
        return "group name : " + this.groupName;
    }
}
