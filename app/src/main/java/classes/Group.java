package classes;

import java.util.ArrayList;

public class Group {
    private String groupName, groupId, managerId;
    private ArrayList<String> members;

    public Group(String groupName, String groupId, String managerId) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.managerId = managerId;
        this.members = new ArrayList<String>();
    }
    public Group(String groupName, String groupId, String managerId,ArrayList<String> members) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.managerId = managerId;
        this.members = members;
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

    public String getManagerId() {
        return this.managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
    public void addMembers(String joinMember){
        this.members.add(joinMember);
    }
    public void deleteFromUserGroups(String deleteMember){
        this.members.remove(deleteMember);
    }

    public ArrayList<String> getMembers() {
        return members;
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
        return "user name : " + this.groupName
                + "\n first name :"+ this.groupId
                +"\n last name: " + this.managerId
                +"\n members: " + this.members.toString();
    }
}
