package com.yopachara.health.demo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.SnackBar;
import com.yopachara.health.demo.Model.FoodModel;
import com.yopachara.health.demo.Service.HealthService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FoodFragment extends Fragment {

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private FoodAdapter mAdapter;
    private FoodModel foodModel;
    String API = "http://pachara.me:3000";
    SnackBar mSnackBar;

    public static FoodFragment newInstance() {
        FoodFragment fragment = new FoodFragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_food, container, false);

        getFoods(v);



        return v;
    }

    private void getFoods(final View v){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);

        api.getFoods( new Callback<FoodModel>() {


            @Override
            public void success(FoodModel foodModel, Response response) {
                ArrayList<FoodModel.Foods> foods = foodModel.getObjects();

				Log.d("Success", "Foods size "+foods.size());



                mRecyclerView = (RecyclerView) v.findViewById(R.id.foodList);

                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(v.getContext());
                mRecyclerView.setLayoutManager(mLayoutManager);

                mAdapter = new FoodAdapter(v.getContext() , foods);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setHasFixedSize(true);


            }

            @Override
            public void failure(RetrofitError error) {

                Log.d("Error", error.toString());

            }
        });
    }


}
