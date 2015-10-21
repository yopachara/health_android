package com.yopachara.health.demo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

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
    private SwipeRefreshLayout mSwipeRefreshLayout;
    int totalcal = 0;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_history, container, false);
        if (historyModel != null) {
            Log.d("Check Value in model", historyModel.getObjects().get(0).getFoodname());
        } else {
            Log.d("Fail", "history model is null");
        }
        getHistory(v);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeHistoryContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHistory(v);
            }
        });


        return v;
    }

    public void getHistory(final View v) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);
        api.getHistorys(new Callback<HistoryModel>() {
            @Override
            public void success(HistoryModel historyModel, Response response) {
                ArrayList<HistoryModel.History> history = historyModel.getObjects();

                Log.d("Success", "History size " + history.size());

                mRecyclerView = (RecyclerView) v.findViewById(R.id.historyList);

//                mRecyclerView.setHasFixedSize(true);

                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);

                mAdapter = new HistoryAdapter(getActivity(), history,getFragmentManager());
                mRecyclerView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
//                final HashMap<Integer, Integer> classes = new HashMap<Integer, Integer>();
//
//                for(int i = 0 ;i < history.size()-1;i++){
//                    Log.d("Date",history.get(i).getDate());
//
//                    String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//                    SimpleDateFormat format = new SimpleDateFormat(pattern);
//                    try {
//                        Date date = format.parse(history.get(i).getDate());
//                        Log.d("Date Format",date.toString());
//                        Log.d("Day", date.getDate() + "");
//                        int x = date.getDate();
//                        int cal = Integer.parseInt(history.get(i).getCal());
//
//                        if (!classes.containsKey(x)) {
//                            classes.put(x,cal );
//                        }else {
//                            classes.put(x, classes.get(x) + cal);
//                        }
//
//
//                    } catch (java.text.ParseException e) {
//                        //handle exception
//                        Log.d("Error ",e.toString());
//                    }
//
//                    totalcal = totalcal+ Integer.parseInt(history.get(i).getCal());
//                }
            }

            @Override
            public void failure(RetrofitError error) {

                Log.d("Error", error.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }

        });
    }




}
