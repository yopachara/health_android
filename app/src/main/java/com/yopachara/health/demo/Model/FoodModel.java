package com.yopachara.health.demo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yopachara on 9/16/15 AD.
 */
public class FoodModel {

    @SerializedName("result")
    private ArrayList<Foods> foods;

    public ArrayList<Foods> getObjects() {
        return foods;
    }

    public String getId(){
        return foods.getClass().getName();
    }

    private String message;

    public String getMessage() {
        return message;
    }

    public class Foods {


        @SerializedName("_id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("type")
        private String type;
        @SerializedName("classifier")
        private String classifier;
        @SerializedName("cal")
        private double cal;
        @SerializedName("protein")
        private double protein;
        @SerializedName("carbo")
        private double carbo;
        @SerializedName("fat")
        private double fat;
        @SerializedName("weight")
        private double weight;

        public String getId() {
            return id;
        }

        public String getName() {
            return this.name;
        }

        public String getType() {
            return type;
        }

        public String getClassifier() {
            return classifier;
        }

        public double getCal() {
            return cal;
        }

        public double getProtein() {
            return protein;
        }

        public double getCarbo() {
            return carbo;
        }

        public double getFat() {
            return fat;
        }

        public double getWeight() {
            return weight;
        }

    }

}
