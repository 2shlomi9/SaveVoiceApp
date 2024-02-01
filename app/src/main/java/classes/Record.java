package classes;

import java.util.ArrayList;

public class Record {
    private String SendInGroupId, SenderId, url, RecordTime, RecordId , AudioName;
    private ArrayList<String> recipientOfTheMessage;
    public Record(){
        this.recipientOfTheMessage = new ArrayList<String>();
    }
    public Record(String AudioName, String RecordId, String RecordTime, String url, String SenderId, String SendInGroupId) {
        this.AudioName = AudioName;
        this.RecordId = RecordId;
        this.RecordTime = RecordTime;
        this.url = url;
        this.SenderId = SenderId;
        this.SendInGroupId = SendInGroupId;
        this.recipientOfTheMessage = new ArrayList<String>();
    }
    public Record(String AudioName, String RecordId, String RecordTime, String url, String SenderId, String SendInGroupId, ArrayList<String> recipientOfTheMessage) {
        this.AudioName = AudioName;
        this.RecordId = RecordId;
        this.RecordTime = RecordTime;
        this.url = url;
        this.SenderId = SenderId;
        this.SendInGroupId = SendInGroupId;
        this.recipientOfTheMessage = recipientOfTheMessage;
    }

    public String getSendInGroupId() {
        return SendInGroupId;
    }

    public void setSendInGroupId(String sendInGroupId) {
        SendInGroupId = sendInGroupId;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRecordTime() {
        return RecordTime;
    }

    public void setRecordTime(String recordTime) {
        RecordTime = recordTime;
    }

    public String getRecordId() {
        return RecordId;
    }

    public void setRecordId(String recordId) {
        RecordId = recordId;
    }

    public String getAudioName() {
        return AudioName;
    }

    public void setAudioName(String audioName) {
        AudioName = audioName;
    }

    public ArrayList<String> getRecipientOfTheMessage() {
        return recipientOfTheMessage;
    }

    public void setRecipientOfTheMessage(ArrayList<String> recipientOfTheMessage) {
        this.recipientOfTheMessage = recipientOfTheMessage;
    }
    public boolean IsExistInMember(String idGetrs){
        if (this.recipientOfTheMessage.contains(idGetrs)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String toString() {
        return "Record{" +
                "SendInGroupId='" + SendInGroupId + '\'' +
                ", SenderId='" + SenderId + '\'' +
                ", url='" + url + '\'' +
                ", RecordTime='" + RecordTime + '\'' +
                ", RecordId='" + RecordId + '\'' +
                ", AudioName='" + AudioName + '\'' +
                ", recipientOfTheMessage=" + recipientOfTheMessage.toString() +
                '}';
    }
}
