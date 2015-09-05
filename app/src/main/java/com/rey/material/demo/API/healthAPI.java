package com.rey.material.demo.API;

/**
 * Created by yopachara on 9/5/15 AD.
 */
import com.rey.material.demo.Model.healthModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;


public interface healthAPI {
    @GET("/")
    public void getFeeds(Callback<healthModel> response);
}
