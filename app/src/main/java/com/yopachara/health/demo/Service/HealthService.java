package com.yopachara.health.demo.Service;

/**
 * Created by yopachara on 9/5/15 AD.
 */
import com.yopachara.health.demo.Model.FoodModel;
import com.yopachara.health.demo.Model.HealthModel;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;


public interface HealthService {
    @GET("/")
    public void getFeeds(Callback<HealthModel> response);

    @FormUrlEncoded
    @POST("/api/foods/search")
    public void
    postSearch(@Field("search") String txt,Callback<FoodModel> response);

    @GET("/api/foods/")
    public void getFoods(Callback<HealthModel> response);
}   