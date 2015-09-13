package com.yopachara.health.demo.Service;

/**
 * Created by yopachara on 9/5/15 AD.
 */
import com.yopachara.health.demo.Model.HealthModel;

import retrofit.Callback;
import retrofit.http.GET;


public interface HealthService {
    @GET("/")
    public void getFeeds(Callback<HealthModel> response);
}   