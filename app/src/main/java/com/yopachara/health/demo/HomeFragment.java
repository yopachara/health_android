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
import android.view.animation.AnticipateInterpolator;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
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
    DecoView mDecoView;
    /**
     * Maximum value for each data series in the {@link DecoView}. This can be different for each
     * data series, in this example we are applying the same all data series
     */
    private final float mSeriesMax = 100f;

    private int mBackIndex;
    private int fatIndex;
    private int proteinIndex;
    private int carboIndex;
    int totalcal = 0;
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
//        calDeco = (DecoView)v.findViewById(R.id.dynamicArcView);
        mDecoView = (DecoView)v.findViewById(R.id.dynamicArcView);


        final TextView text_totalcal = (TextView) v.findViewById(R.id.totalcal);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeHomeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRefreshDeco();
            }
        });

//        // Create background track
//        calDeco.addSeries(new SeriesItem.Builder(Color.argb(255,162,184,154))
//                .setRange(0, 100, 100)
//                .setInitialVisibility(false)
//                .setLineWidth(32f)
//                .build());
//
//
//        //Create data series track
//        SeriesItem calSeries = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
//                .setRange(0, 100, 0)
//                .setLineWidth(32f)
//                .build();
//
//
//        calIndex = calDeco.addSeries(calSeries);
//
//
//        calDeco.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
//                .setDelay(1000)
//                .setDuration(2000)
//                .build());
//
//        final TextView textPercent = (TextView) v.findViewById(R.id.textValue);
//        if (textPercent != null) {
//            textPercent.setText("");
//            addProgressListener(calSeries, textPercent,text_totalcal, "%.0f%%");
//        }

        // Create required data series on the DecoView
        createBackSeries(v);
        createDataSeries1(v);
        createDataSeries2(v);
        createDataSeries3(v);
        getRefreshDeco();


        return v;
    }

    public void getRefreshDeco(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);
        api.getHistoryToday(new Callback<HistoryModel>() {
            @Override
            public void success(HistoryModel historyModel, Response response) {
                Log.d("Success", "home fragment refresh");
                ArrayList<HistoryModel.History> histories = historyModel.getObjects();
                calculateNutrition(histories);

                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error",error.toString());
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    private void calculateNutrition(ArrayList<HistoryModel.History> histories){
        int cal= 0;
        float protein = 0;
        float fat = 0;
        float carbo = 0;
        if(histories.size()==0){
            Log.d("Success", "today is no food");
//                    addDeco(cal);
        }
        for(int i=0;i<histories.size();i++){
            cal = cal+histories.get(i).getCalInt();
            protein = protein+histories.get(i).getProteinInt();
            fat = fat+histories.get(i).getFatInt();
            carbo = carbo+histories.get(i).getCarboInt();
            Log.d("Success","have "+i+" total is "+cal);

            if (i==histories.size()-1){
                Log.d("Protein", "total is "+protein);
                Log.d("Fat","total is "+fat);
                Log.d("Carbohyhrate","total is "+carbo);
                totalcal = cal;
                int persentCal = (cal*100)/1814;
                float persentFat = (fat*9*100)/1814;
                float persentProtein = (protein*4*100)/1814;
                float persentCarbo = (carbo*4*100)/1814;
                Log.d("Persent","Fat "+persentFat+" Pro "+persentProtein+" Carb "+persentCarbo);
                createEvents(persentCal, persentProtein, persentFat, persentCarbo);
            }
        }
    }

    public void addDeco(int num){
        Log.d("Add Deco", "value is " + num+"%");
        calDeco.addEvent(new DecoEvent.Builder(num).setIndex(carboIndex).setDuration(3000).build());
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
                        text_totalcal.setText((percentComplete * totalcal) + " cal");
                    } else {
                        view.setText(String.format(format, currentPosition));
                        text_totalcal.setText(totalcal + " cal");
                    }
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

    }

    private void createBackSeries(View v) {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.argb(255, 162, 184, 154))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries1(View v) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textPercentage = (TextView) v.findViewById(R.id.proteinText);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                textPercentage.setText(String.format("Protein %.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });


//        final TextView textToGo = (TextView) v.findViewById(R.id.textRemaining);
//        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
//            @Override
//            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
//                textToGo.setText(String.format("%.1f Km to goal", seriesItem.getMaxValue() - currentPosition));
//
//            }
//
//            @Override
//            public void onSeriesItemDisplayProgress(float percentComplete) {
//
//            }
//        });

//        final TextView textActivity1 = (TextView) findViewById(R.id.textActivity1);
//        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
//            @Override
//            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
//                textActivity1.setText(String.format("%.0f Km", currentPosition));
//            }
//
//            @Override
//            public void onSeriesItemDisplayProgress(float percentComplete) {
//
//            }
//        });

        fatIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries2(View v) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF4444"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textActivity2 = (TextView) v.findViewById(R.id.fatText);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity2.setText(String.format("Fat %.0f%%", currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        proteinIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries3(View v) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FF6699FF"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textActivity3 = (TextView) v.findViewById(R.id.carboText);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity3.setText(String.format("Carbohydrate %.0f%%", currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        carboIndex = mDecoView.addSeries(seriesItem);
    }

    private void createEvents(int cal,float protein,float fat,float carbo) {
        Log.d("Para CreateEvents","Fat "+fat+" Pro "+protein+" Carb "+carbo);
        float persentFat = fat+carbo+protein;
        float persentPro = protein+carbo;
        float persentCarb = carbo;
        Log.d("CreateEvents","Fat "+persentFat+" Pro "+persentPro+" Carb "+persentCarb);
        mDecoView.executeReset();

        mDecoView.addEvent(new DecoEvent.Builder(mSeriesMax)
                .setIndex(mBackIndex)
                .setDuration(3000)
                .setDelay(100)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(fatIndex)
                .setDuration(2000)
                .setDelay(1250)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(persentFat)
                .setIndex(fatIndex)
                .setDelay(3250)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(proteinIndex)
                .setDuration(1000)
                .setEffectRotations(1)
                .setDelay(5000)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(persentPro)
                .setIndex(proteinIndex)
                .setDelay(8500)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(carboIndex)
                .setDuration(1000)
                .setEffectRotations(1)
                .setDelay(9000)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(persentCarb).setIndex(carboIndex).setDelay(14000).build());

//        mDecoView.addEvent(new DecoEvent.Builder(0).setIndex(carboIndex).setDelay(18000).build());
//
//        mDecoView.addEvent(new DecoEvent.Builder(0).setIndex(proteinIndex).setDelay(18000).build());
//
//        mDecoView.addEvent(new DecoEvent.Builder(0)
//                .setIndex(fatIndex)
//                .setDelay(20000)
//                .setDuration(1000)
//                .setInterpolator(new AnticipateInterpolator())
//                .setListener(new DecoEvent.ExecuteEventListener() {
//                    @Override
//                    public void onEventStart(DecoEvent decoEvent) {
//
//                    }
//
//                    @Override
//                    public void onEventEnd(DecoEvent decoEvent) {
//                        resetText(getView());
//                    }
//                })
//                .build());

//        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
//                .setIndex(fatIndex)
//                .setDelay(21000)
//                .setDuration(3000)
//                .setDisplayText("GOAL!")
//                .setListener(new DecoEvent.ExecuteEventListener() {
//                    @Override
//                    public void onEventStart(DecoEvent decoEvent) {
//
//                    }
//
//                    @Override
//                    public void onEventEnd(DecoEvent decoEvent) {
////                        createEvents(getView());
//                    }
//                })
//                .build());

//        resetText(getView());
    }

    private void resetText(View v) {
        ((TextView) v.findViewById(R.id.calText)).setText("");
        ((TextView) v.findViewById(R.id.fatText)).setText("");
        ((TextView) v.findViewById(R.id.carboText)).setText("");
        ((TextView) v.findViewById(R.id.proteinText)).setText("");
//        ((TextView) findViewById(R.id.textRemaining)).setText("");
    }


}
