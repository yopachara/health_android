package com.yopachara.health.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.yopachara.health.demo.Model.HistoryModel;
import com.yopachara.health.demo.Model.UserModel;
import com.yopachara.health.demo.Service.HealthService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class HomeFragment extends Fragment {
    String API = "http://pachara.me:3000";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    protected boolean mUpdateListeners = true;
    DecoView calDeco;
    DecoView proteinDeco;
    DecoView fatDeco;
    DecoView carboDeco;
    int calIndex;
    int proteinIndex;
    int carboIndex;
    int fatIndex;
    int totalcal = 0;
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        calDeco = (DecoView)v.findViewById(R.id.dynamicArcView);
        proteinDeco = (DecoView)v.findViewById(R.id.proteinDeco);
        fatDeco = (DecoView)v.findViewById(R.id.fatDeco);
        carboDeco = (DecoView)v.findViewById(R.id.carboDeco);

        final TextView text_totalcal = (TextView) v.findViewById(R.id.totalcal);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeHomeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRefreshDeco();
            }
        });

        // Create background track
        calDeco.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .build());
        proteinDeco.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .build());
        fatDeco.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .build());
        carboDeco.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .build());

        //Create data series track
        SeriesItem calSeries = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();
        SeriesItem proteinSeries = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();
        SeriesItem fatSeries = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();
        SeriesItem carboSeries = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        calIndex = calDeco.addSeries(calSeries);
        proteinIndex = proteinDeco.addSeries(proteinSeries);
        fatIndex = fatDeco.addSeries(fatSeries);
        carboIndex = carboDeco.addSeries(carboSeries);

        calDeco.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        final TextView textPercent = (TextView) v.findViewById(R.id.textValue);
        if (textPercent != null) {
            textPercent.setText("");
            addProgressListener(calSeries, textPercent,text_totalcal, "%.0f%%");
        }

        proteinDeco.addEvent(new DecoEvent.Builder(25).setIndex(proteinIndex).setDelay(4000).build());
        carboDeco.addEvent(new DecoEvent.Builder(14).setIndex(carboIndex).setDelay(15000).build());
        fatDeco.addEvent(new DecoEvent.Builder(14).setIndex(fatIndex).setDelay(15000).build());


        return v;
    }

    public void getRefreshDeco(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);
        api.getHistoryToday(new Callback<HistoryModel>() {
            @Override
            public void success(HistoryModel historyModel, Response response) {
                Log.d("Success","home fragment refresh");
                ArrayList<HistoryModel.History> histories = historyModel.getObjects();
                int cal = 0;
                if(histories.size()==0){
                    Log.d("Success","today is no food");
                    addDeco(cal);
                }
                for(int i=0;i<histories.size();i++){
                    cal = cal+histories.get(i).getCalInt();
                    Log.d("Success","total cal today is "+cal);

                    if (i==histories.size()-1){
                        totalcal = cal;
                        int persent = (cal*100)/1814;
                        addDeco(persent);
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error",error.toString());
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public void addDeco(int num){
        Log.d("Add Deco", "value is " + num+"%");
        calDeco.addEvent(new DecoEvent.Builder(num).setIndex(calIndex).setDuration(3000).build());
    }

    protected void addProgressListener(@NonNull final SeriesItem seriesItem, @NonNull final TextView view,final TextView text_totalcal, @NonNull final String format) {
        if (format.length() <= 0) {
            throw new IllegalArgumentException("String formatter can not be empty");
        }

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (mUpdateListeners) {
                    if (format.contains("%%")) {
                        // We found a percentage so we insert a percentage
                        float percentFilled = ((currentPosition - seriesItem.getMinValue()) /
                                (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                        view.setText(String.format(format, percentFilled * 100f));
                        text_totalcal.setText((percentComplete*totalcal)+" cal");
                    } else {
                        view.setText(String.format(format, currentPosition));
                        text_totalcal.setText(totalcal+" cal");
                    }
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

    }


}
