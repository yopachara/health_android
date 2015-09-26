package com.yopachara.health.demo.Model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yopachara on 9/25/15 AD.
 */
public class UserModel {
    @SerializedName("result")
    private ArrayList<User> user;

    public ArrayList<User> getObjects() {
        return user;
    }

    public String getId(){
        return user.getClass().getName();
    }

    public class User {


        @SerializedName("_id")
        private String id;

        @SerializedName("username")
        private String username;

        @SerializedName("password")
        private String password;

        @SerializedName("sex")
        private String sex;

        @SerializedName("weight")
        private int weight;

        @SerializedName("height")
        private int height;

        @SerializedName("birthdate")
        private Timestamp birthdate;

        @SerializedName("createAt")
        private Date createAt;

        @SerializedName("type")
        private String type;

        public String getId() {
            return id;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getSex() {
            return sex;
        }

        public int getWeight() {
            return weight;
        }

        public int getHeight() {
            return height;
        }

        public Timestamp getBirthdate() {
            return birthdate;
        }

        public Date getCreateAt() {
            return createAt;
        }

        public String getType() {
            return type;
        }

    }
}
