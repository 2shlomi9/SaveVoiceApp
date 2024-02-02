package classes;

import java.util.ArrayList;

public class Record {
    private String GroupId, SenderId, url, RecordTime, RecordId , AudioName;
    private ArrayList<String> sent_users, delivered_users;
    public Record(){
        this.sent_users = new ArrayList<String>();
        this.delivered_users = new ArrayList<String>();
    }
    public Record(String AudioName, String RecordId, String RecordTime, String url, String SenderId, String SendInGroupId) {
        this.AudioName = AudioName;
        this.RecordId = RecordId;
        this.RecordTime = RecordTime;
        this.url = url;
        this.SenderId = SenderId;
        this.GroupId = SendInGroupId;
        this.sent_users = new ArrayList<String>();
        this.delivered_users = new ArrayList<String>();
    }
    public Record(String AudioName, String RecordId, String RecordTime, String url, String SenderId, String SendInGroupId,ArrayList<String> sent_users,ArrayList<String>  delivered_users) {
        this.AudioName = AudioName;
        this.RecordId = RecordId;
        this.RecordTime = RecordTime;
        this.url = url;
        this.SenderId = SenderId;
        this.GroupId = SendInGroupId;
        this.sent_users = sent_users;
        this.delivered_users = delivered_users;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        this.GroupId = groupId;
    }

    public String getSenderId() {
        return this.SenderId;
    }

    public void setSenderId(String senderId) {
        this.SenderId = senderId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRecordTime() {
        return this.RecordTime;
    }

    public void setRecordTime(String recordTime) {
        this.RecordTime = recordTime;
    }

    public String getRecordId() {
        return this.RecordId;
    }

    public void setRecordId(String recordId) {
        this.RecordId = recordId;
    }

    public String getAudioName() {
        return this.AudioName;
    }

    public void setAudioName(String audioName) {
        this.AudioName = audioName;
    }

    public ArrayList<String> getDelivered_users() {
        return this.delivered_users;
    }

    public void setDelivered_users(ArrayList<String> delivered_users) {
        this.delivered_users = delivered_users;
    }
    public ArrayList<String> getSent_users() {
        return this.sent_users;
    }

    public void setSent_users(ArrayList<String> sent_users) {
        this.sent_users = sent_users;
    }
    public boolean is_sent_to_user(String idGetrs){
        return this.sent_users.contains(idGetrs);
    }
    public boolean is_delivered_to_user(String idGetrs){
        return this.delivered_users.contains(idGetrs);
    }
    public boolean send_to_user(String uid){
        if(!this.is_sent_to_user(uid)) {
            this.sent_users.add(uid);
            return true;
        }
        return false;
    }

    public boolean deliver_to_user(String uid){
        if(!this.is_sent_to_user(uid)||this.is_delivered_to_user(uid)) {
            return false;
        }
        this.sent_users.remove(uid);
        this.delivered_users.add(uid);
        return true;
    }

    @Override
    public String toString() {
        return "Record{" +
                "SendInGroupId='" + GroupId + '\'' +
                ", SenderId='" + SenderId + '\'' +
                ", url='" + url + '\'' +
                ", RecordTime='" + RecordTime + '\'' +
                ", RecordId='" + RecordId + '\'' +
                ", AudioName='" + AudioName + '\'' +
                ", sent users=" + sent_users.toString() + '\n'+
                ", delivered users=" + delivered_users.toString() +
                '}';
    }
}
