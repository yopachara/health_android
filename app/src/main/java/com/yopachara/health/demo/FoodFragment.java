package com.yopachara.health.demo;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.greenfrvr.rubberloader.RubberLoaderView;
import com.rey.material.widget.SnackBar;
import com.yopachara.health.demo.Model.FoodModel;
import com.yopachara.health.demo.Service.HealthService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FoodFragment extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private FoodAdapter mAdapter;
    private FoodModel foodModels;
    String API = "http://pachara.me:3000";
    SnackBar mSnackBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<FoodModel.Foods> foods= new ArrayList<FoodModel.Foods>();
    private String queryDecrease = "";
    private String queryTmp;
    private Handler mHandler = new Handler();
    Runnable mFilterTask = new Runnable() {

        @Override
        public void run() {
            getFoodSearch(queryTmp);
        }
    };

    public static FoodFragment newInstance() {
        FoodFragment fragment = new FoodFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_food, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.foodList);
        getFoods(v);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeFoodContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFoods(v);
            }
        });
        setHasOptionsMenu(true);


        return v;
    }

    public void getFoods(final View v) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);

        api.getFoods(new Callback<FoodModel>() {


            @Override
            public void success(FoodModel foodModel, Response response) {
                foodModels = foodModel;
                foods.addAll(foodModel.getObjects());
                Log.d("Success", "Foods size " + foods.size());


                mLayoutManager = new LinearLayoutManager(v.getContext());
                mRecyclerView.setLayoutManager(mLayoutManager);

                mAdapter = new FoodAdapter(v.getContext(), foods);
                mRecyclerView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);

                RubberLoaderView l;
                FrameLayout frame_loader;
                l = (RubberLoaderView)getActivity().findViewById(R.id.loader);
                frame_loader = (FrameLayout)getActivity().findViewById(R.id.frame_loader);
                l.setVisibility(View.GONE);
                frame_loader.setVisibility(View.GONE);


            }

            @Override
            public void failure(RetrofitError error) {

                Log.d("Error", error.toString());
                mSwipeRefreshLayout.setRefreshing(false);

            }

        });
    }

    public void getFoodSearch(final String query) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);
        api.getFoods(new Callback<FoodModel>() {

            @Override
            public void success(FoodModel foodModel, Response response) {
                foodModels = foodModel;
                foods = foodModel.getObjects();
                Log.d("Success", "Foods size " + foods.size());

                final List<FoodModel.Foods> filteredModelList = filter(foods, query);
                mAdapter.animateTo(filteredModelList);
                mRecyclerView.scrollToPosition(0);
            }

            @Override
            public void failure(RetrofitError error) {

                Log.d("Error", error.toString());
                mSwipeRefreshLayout.setRefreshing(false);

            }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_food, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Log.d("onQueqyTextChange", query);

        if (query.length()<queryDecrease.length()){
            queryTmp = query;
            mHandler.removeCallbacks(mFilterTask);
            mHandler.postDelayed(mFilterTask, 500);
        }
        else {
            final List<FoodModel.Foods> filteredModelList = filter(foods, query);
            mAdapter.animateTo(filteredModelList);
            mRecyclerView.scrollToPosition(0);
        }
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("onQueryTextSubmit", query);
        return false;
    }

    private List<FoodModel.Foods> filter(List<FoodModel.Foods> models, String query) {
        Log.d("filter", query);
        query = query.toLowerCase();
        queryDecrease = query;
        final List<FoodModel.Foods> filteredModelList = new ArrayList<>();
        for (FoodModel.Foods model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        Log.d("Filter Size", filteredModelList.size() + "");
        return filteredModelList;
    }


}
