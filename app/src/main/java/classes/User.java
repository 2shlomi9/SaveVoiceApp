package classes;
import android.text.TextUtils;
import android.util.Patterns;
import java.util.ArrayList;

/**
 * class Request
 */
public class User {

    private String firstName, lastName, userName, email;
    private ArrayList<String> userMangerGroups;
    private ArrayList<String> userGroups;

    public User(String firstName, String lastName, String userName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.userMangerGroups = new ArrayList<String>();
        this.userGroups = new ArrayList<String>();
    }
    public User(String firstName, String lastName, String userName, String email, ArrayList<String> userMangerGroups,ArrayList<String> userGroups) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.userMangerGroups = userMangerGroups;
        this.userGroups = userGroups;
    }

    // ---- GET(ERS) & SET(ERS) ---- //
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
                return "Full name not valid!";
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
            return "Please enter at least 6 characters.";
        return "accept";
    }
    public String toString(){
        return "user name : " + this.userName
                + "\n first name :"+ this.firstName
                +"\n last name: " + this.lastName
                +"\n group That" + this.userName + "manager" + this.userMangerGroups.toString()
                +"\n group That" + this.userName + " is member in them" + this.userMangerGroups.toString();
    }

}