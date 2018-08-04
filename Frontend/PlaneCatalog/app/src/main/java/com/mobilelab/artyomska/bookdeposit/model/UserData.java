package com.mobilelab.artyomska.bookdeposit.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * Created by Artyomska on 10/16/2017.
 */
public class UserData
{
    private int ID;
    private String username;
    private String userpass;
    private String email;
    private byte[] userPic;
    private String noOfFriends;
    private String userOverview;
    private LocalDate joinedDate;

    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDate joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getUserOverview() {
        return userOverview;
    }

    public void setUserOverview(String userOverview) {
        this.userOverview = userOverview;
    }

    public String getNoOfFriends() {
        return noOfFriends;
    }

    public void setNoOfFriends(String noOfFriends) {
        this.noOfFriends = noOfFriends;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String password) {
        this.userpass = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public byte[] getUserPic() {
        return userPic;
    }

    public void setUserPic(byte[] userPic) {
        this.userPic = userPic;
    }

    public UserData(String username, String userpass, String email) {
        this.username = username;
        this.userpass = userpass;
        this.email = email;
    }

    public UserData(int ID, String username, String email, byte[] userPic) {
        this.ID = ID;
        this.username = username;
        this.email = email;
        this.userPic = userPic;
    }

    public UserData() {

    }


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + userpass + '\'' +
                '}';
    }
}
