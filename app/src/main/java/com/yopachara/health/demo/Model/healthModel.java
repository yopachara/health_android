package com.yopachara.health.demo.Model;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * Created by yopachara on 9/5/15 AD.
 */


public class HealthModel {
    private String message;

    private String name;

    private String type;

    private String classifier;

    private double cal;
    private double protein;
    private double carbo;
    private double fat;
    private double weight;

    private Array result;

    /**
     *
     * @return
     * The login
     */
    public String getTexts() {
        return message;
    }
    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
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