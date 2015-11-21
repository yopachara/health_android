package com.yopachara.health.demo.Model;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.text.ParseException;

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

        public Date getDateFormat() {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                Date date1 = simpleDateFormat.parse(getDate());
                return  date1;
            } catch (ParseException e) {              // Insert this block.
                // TODO Auto-generated catch block
                e.printStackTrace();
                return  null;
            }

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

        public int getCalInt() {
            String tmp = cal;
            double calInt = Double.parseDouble(cal);
            return (int)calInt;
        }

        public float getProteinInt() {
            return Float.parseFloat(protein);
        }

        public float getFatInt() {
            return Float.parseFloat(fat);
        }

        public float getCarboInt() {
            return Float.parseFloat(carbo);
        }

    }


}