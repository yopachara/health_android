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
        private float cal;
        @SerializedName("protein")
        private float protein;
        @SerializedName("carbo")
        private float carbo;
        @SerializedName("fat")
        private float fat;
        @SerializedName("weight")
        private float weight;

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

        public float getCal() {
            return cal;
        }

        public float getProtein() {
            return protein;
        }

        public float getCarbo() {
            return carbo;
        }

        public float getFat() {
            return fat;
        }

        public float getWeight() {
            return weight;
        }

    }

}
