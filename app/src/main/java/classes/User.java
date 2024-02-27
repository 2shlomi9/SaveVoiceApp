package classes;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * class Request
 */
public class User {

    private String Uid, firstName, lastName, userName, email;
    private ArrayList<String> userMangerGroups;
    private ArrayList<String> userGroups;

    public User() {
        this.userMangerGroups = new ArrayList<String>();
        this.userGroups = new ArrayList<String>();
    }
    public User(String Uid, String firstName, String lastName, String userName, String email) {
        this.Uid = Uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.userMangerGroups = new ArrayList<String>();
        this.userGroups = new ArrayList<String>();
    }
    public User(String Uid, String firstName, String lastName, String userName, String email, ArrayList<String> userMangerGroups,ArrayList<String> userGroups) {
        this.Uid = Uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.userMangerGroups = userMangerGroups;
        this.userGroups = userGroups;
    }

    // ---- GET(ERS) & SET(ERS) ---- //
    public String getUid() {
        return this.Uid;
    }

    public void setUid(String Uid) {
        this.Uid = Uid;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getuserName() {
        return this.userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return this.email;
    }


    public ArrayList<String> getUserMangerGroups() {
        return userMangerGroups;
    }

    public void setUserMangerGroups(ArrayList<String> userMangerGroups) {
        this.userMangerGroups = userMangerGroups;
    }

    public ArrayList<String> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(ArrayList<String> userGroups) {
        this.userGroups = userGroups;
    }
    public void addGroup(String groupId){
        this.userGroups.add(groupId);
    }
    public void exitGroup(String groupId){
        this.userGroups.remove(groupId);
    }
    public void deleteGroup(String groupId){
        this.userGroups.remove(groupId);
    }

    // ---- END OF GET(ERS) & SET(ERS) ---- //


    /**
     * check full Name of User (need be correct)
     *
     * @param name name of user
     * @return (String) accept/not
     */
    public static String check_name(String name) {
        if (TextUtils.isEmpty(name))
            return "Please enter full name.";
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == '\n')
                return "Name not valid!";
        }
        return "accept";
    }

    /**
     * check if email of User is valid (need be correct)
     *
     * @param email email of user
     * @return (String) accept/not
     */
    public static String check_email(String email) {
        if (TextUtils.isEmpty(email))
            return "Please enter email.";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Please enter valid email.";
        return "accept";
    }

    /**
     * check if password of User is valid (need be correct)
     *
     * @param password password of user
     * @return (String) accept/not
     */
    public static String check_pass(String password) {
        if (TextUtils.isEmpty(password))
            return "Please enter password.";
        if (password.length() < 6)
            return "Please enter at least 6 characters for password.";
        return "accept";
    }
    public static String check_username(String username) {
        if (TextUtils.isEmpty(username))
            return "Please enter user name.";
        if (username.length() < 4)
            return "Please enter at least 4 characters for user name.";

        return "accept";

    }
    public String toString(){
        return "user name : " + this.userName
                + "\n first name :"+ this.firstName
                +"\n last name: " + this.lastName
                +"\n group That" + this.userName + "manager" + this.userMangerGroups.toString()
                +"\n group That" + this.userName + " is member in them" + this.userMangerGroups.toString();
    }

    public String getFullName() {
        return this.firstName +" " +this.lastName;
    }
}