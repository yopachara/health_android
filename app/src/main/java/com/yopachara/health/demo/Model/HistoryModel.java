package com.yopachara.health.demo.Model;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by yopachara on 9/5/15 AD.
 */


public class HistoryModel {
    @SerializedName("result")
    private ArrayList<History> history;

    public ArrayList<History> getObjects() {
        return history;
    }

    public String getId(){
        return history.getClass().getName();
    }


    public class History {


        @SerializedName("_id")
        private String id;

        @SerializedName("foodname")
        private String foodname;

        @SerializedName("username")
        private String username;

        @SerializedName("date")
        private String date;

        @SerializedName("cal")
        private String cal;

        @SerializedName("protein")
        private String protein;

        @SerializedName("fat")
        private String fat;

        @SerializedName("carbo")
        private String carbo;


        public String getId() {
            return id;
        }

        public String getDate() {
            return date;
        }

        public String getUsername() {
            return username;
        }

        public String getCal() {
            return cal;
        }

        public String getFoodname() {
            return foodname;
        }

        public String getProtein() {
            return protein;
        }

        public String getFat() {
            return fat;
        }

        public String getCarbo() {
            return carbo;
        }

    }


}