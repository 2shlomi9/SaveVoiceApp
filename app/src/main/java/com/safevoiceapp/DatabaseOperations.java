package com.safevoiceapp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class DatabaseOperations {

    // Simulated database storage
    private Map<String, Map<String, Object>> usersCollection = new HashMap<>();
    private Map<String, Map<String, Object>> groupsCollection = new HashMap<>();
    private Map<String, Map<String, Object>> recordsCollection = new HashMap<>();

    public DatabaseOperations() {
        // Initialize the database references
        usersCollection = (Map<String, Map<String, Object>>) FirebaseDatabase.getInstance().getReference("users");
        groupsCollection = (Map<String, Map<String, Object>>) FirebaseDatabase.getInstance().getReference("groups");
        recordsCollection = (Map<String, Map<String, Object>>) FirebaseDatabase.getInstance().getReference("records");
    }

    ////////////////////////////////////// Users Collection Operations//////////////////////////////
    public void addUser(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        usersCollection.put(userId, user);
    }

    public Map<String, Object> getUser(String userId) {
        return usersCollection.get(userId);
    }

    public void updateUser(String userId, String name, String email) {
        Map<String, Object> user = usersCollection.get(userId);
        if (user != null) {
            user.put("name", name);
            user.put("email", email);
        }
    }

    public void deleteUser(String userId) {
        usersCollection.remove(userId);
    }

    //////////////////////////////////// Groups Collection Operations///////////////////////////////

    public void addGroup(String groupId, String groupName) {
        Map<String, Object> group = new HashMap<>();
        group.put("name", groupName);
        groupsCollection.put(groupId, group);
    }

    public Map<String, Object> getGroup(String groupId) {
        return groupsCollection.get(groupId);
    }

    public void updateGroup(String groupId, String groupName) {
        Map<String, Object> group = groupsCollection.get(groupId);
        if (group != null) {
            group.put("name", groupName);
        }
    }

    public void deleteGroup(String groupId) {
        groupsCollection.remove(groupId);
    }

    /////////////////////////////////// Records Collection Operations///////////////////////////////

    public void addRecord(String recordId, String content) {
        Map<String, Object> record = new HashMap<>();
        record.put("content", content);
        recordsCollection.put(recordId, record);
    }

    public Map<String, Object> getRecord(String recordId) {
        return recordsCollection.get(recordId);
    }

    public void updateRecord(String recordId, String content) {
        Map<String, Object> record = recordsCollection.get(recordId);
        if (record != null) {
            record.put("content", content);
        }
    }

    public void deleteRecord(String recordId) {
        recordsCollection.remove(recordId);
    }

}

