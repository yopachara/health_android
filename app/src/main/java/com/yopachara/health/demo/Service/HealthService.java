package com.yopachara.health.demo.Service;

/**
 * Created by yopachara on 9/5/15 AD.
 */
import com.yopachara.health.demo.Model.FoodModel;
import com.yopachara.health.demo.Model.HistoryModel;
import com.yopachara.health.demo.Model.UserModel;

import java.sql.Timestamp;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;


public interface HealthService {
    @GET("/")
    public void getFeeds(Callback<FoodModel> response);

    @FormUrlEncoded
    @POST("/api/foods/search")
    public void
    postSearch(@Field("search") String txt,Callback<FoodModel> response);

    @GET("/api/foods/")
    public void getFoods(Callback<FoodModel> response);

    @GET("/api/history")
    public void getHistorys(Callback<HistoryModel> response);

    @GET("/api/historytoday")
    public void getHistoryToday(Callback<HistoryModel> response);

    @FormUrlEncoded
    @POST("/api/history")
    public void postHistory(@Field("username")String username,
                            @Field("foodname")String foodname,
                            @Field("cal")float cal,
                            @Field("protein")float protein,
                            @Field("carbo")float carbo,
                            @Field("fat")float fat,
                            Callback<HistoryModel> response);

    @FormUrlEncoded
    @POST("/api/user")
    public void postUser(@Field("username")String username,
                         @Field("password")String password,
                         @Field("sex")String sex,
                         @Field("weight")int weight,
                         @Field("height")int height,
                         @Field("birthdate")Timestamp birthdate,
                         @Field("type")String type,
                         @Field("bmr")int bmr,
                         @Field("bmi")int bmi,
                         Callback<UserModel> response);

    @GET("/api/users")
    public void getUser(@Header("Authorization") String authorization,
                        Callback<UserModel> callback);

    @GET("/api/users/{userid}")
    public void getUserID(@Header("Authorization") String authorization,
                          @Path("userid")String username,
                          Callback<UserModel> callback);
}