package com.yopachara.health.demo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rey.material.widget.SnackBar;
import com.yopachara.health.demo.Model.HistoryModel;
import com.yopachara.health.demo.Service.HealthService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class HistoryFragment extends Fragment {

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;
    private HistoryModel historyModel;
    String API = "http://pachara.me:3000";
    SnackBar mSnackBar;


    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        this.getHistory(v);



        return v;
    }


    private void getHistory(final View v){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);

        api.getHistorys(new Callback<HistoryModel>() {

            @Override
            public void success(HistoryModel historyModel, Response response) {
                ArrayList<HistoryModel.History> history = historyModel.getObjects();

                Log.d("Success", "History size " + history.size());

                mRecyclerView = (RecyclerView) v.findViewById(R.id.historyList);

                mRecyclerView.setHasFixedSize(true);

                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);

                mAdapter = new HistoryAdapter(getActivity(), history);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void failure(RetrofitError error) {

                Log.d("Error", error.toString());

            }
        });
    }
}
