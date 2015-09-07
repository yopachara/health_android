package com.yopachara.health.demo.API;

/**
 * Created by yopachara on 9/5/15 AD.
 */
import com.yopachara.health.demo.Model.healthModel;

import retrofit.Callback;
import retrofit.http.GET;


public interface healthAPI {
    @GET("/")
    public void getFeeds(Callback<healthModel> response);
}
