package com.yopachara.health.demo;

import android.graphics.Color;
import android.graphics.Typeface;
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
    ArrayList<UserModel.User> users;
    TextView textActivity1;

    /**
     * Maximum value for each data series in the {@link DecoView}. This can be different for each
     * data series, in this example we are applying the same all data series
     */
    private final float mSeriesMax = 100f;

    private int mBackIndex;
    private int fatIndex;
    private int proteinIndex;
    private int carboIndex;

    private float fatPercent;
    private float proteinPercent;
    private float carboPercent;
    int totalcal = 0;
    private static final String USER_KEY = "user_key";
    private UserModel.User mUser;
    int weight;
    int height;
    Float bmr;
    Float bmi;
    Typeface font;

    public static HomeFragment newInstance(UserModel.User user) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mUser = (UserModel.User) getArguments().getSerializable(USER_KEY);
//        calDeco = (DecoView)v.findViewById(R.id.dynamicArcView);
        mDecoView = (DecoView)v.findViewById(R.id.dynamicArcView);
        final TextView text_totalcal = (TextView) v.findViewById(R.id.totalcal);
//        UserModel.User user = users.get(0);
        weight = mUser.getWeight();
        height = mUser.getHeight();
        bmr = mUser.getBmr();
        bmi = mUser.getBmi();
        font = Typeface.createFromAsset(getContext().getAssets(), "supermarket.ttf");
        Log.d("User information","Weight "+weight+" Height "+height);


        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeHomeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRefreshDeco();
            }
        });


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

    public void calculateNutrition(ArrayList<HistoryModel.History> histories){
        int cal= 0;
        float protein = 0;
        float fat = 0;
        float carbo = 0;
        if(histories.size()==0){
            Log.d("Success", "today is no food");
            textActivity1.setText("Today is no food");
            createEvents(cal, protein, fat, carbo);
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
                int persentCal = (cal*100)/Math.round(bmr);
                float persentFat = (fat*9*100)/bmr;
                float persentProtein = (protein*4*100)/bmr;
                float persentCarbo = (carbo*4*100)/bmr;
                fatPercent = persentFat;
                proteinPercent = persentProtein;
                carboPercent = persentCarbo;

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
        SeriesItem seriesItem = new SeriesItem.Builder(Color.argb(255, 226,233,223))
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

        final TextView textPercentage = (TextView) v.findViewById(R.id.fatText);
        textPercentage.setTypeface(font);
        final View fat1 = (View)v.findViewById(R.id.fat1);
        final View fat2 = (View)v.findViewById(R.id.fat2);
        final View fat3 = (View)v.findViewById(R.id.fat3);
        final View fat4 = (View)v.findViewById(R.id.fat4);
        final View fat5 = (View)v.findViewById(R.id.fat5);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = (percentComplete/100)*fatPercent*100f;
                textPercentage.setText(String.format("ไขมัน %.0f%%", percentFilled));
                if(percentFilled>=20) {
                    fat1.setBackgroundResource(R.drawable.circle_black);
                }
                if(percentFilled>=40) {
                    fat2.setBackgroundResource(R.drawable.circle_black);
                }
                if(percentFilled>=60) {
                    fat3.setBackgroundResource(R.drawable.circle_black);
                }
                if(percentFilled>=80) {
                    fat4.setBackgroundResource(R.drawable.circle_black);
                }
                if(percentFilled>=100) {
                    fat5.setBackgroundResource(R.drawable.circle_black);
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });


        final TextView textToGo = (TextView) v.findViewById(R.id.totalcal);
        textToGo.setTypeface(font);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textToGo.setText(String.format("%s เหลือแคลอรี่", ((seriesItem.getMaxValue() - currentPosition)/100)*1814));

            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        textActivity1 = (TextView) v.findViewById(R.id.textValue);
        final TextView textCal = (TextView) v.findViewById(R.id.calText);
        final View cal1 = (View)v.findViewById(R.id.cal1);
        final View cal2 = (View)v.findViewById(R.id.cal2);
        final View cal3 = (View)v.findViewById(R.id.cal3);
        final View cal4 = (View)v.findViewById(R.id.cal4);
        final View cal5 = (View)v.findViewById(R.id.cal5);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity1.setText(String.format("%.0f แคลอรี่", (currentPosition/100)*1814));
                textCal.setText(String.format("%.0f แคลอรี่", (currentPosition/100)*1814));
                textActivity1.setTypeface(font);
                textCal.setTypeface(font);
                if(currentPosition>=20) {
                    cal1.setBackgroundResource(R.drawable.circle_black);
                }
                if(currentPosition>=40) {
                    cal2.setBackgroundResource(R.drawable.circle_black);
                }
                if(currentPosition>=60) {
                    cal3.setBackgroundResource(R.drawable.circle_black);
                }
                if(currentPosition>=80) {
                    cal4.setBackgroundResource(R.drawable.circle_black);
                }
                if(currentPosition>=100) {
                    cal5.setBackgroundResource(R.drawable.circle_black);
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        fatIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries2(View v) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF4444"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textActivity2 = (TextView) v.findViewById(R.id.proteinText);
        textActivity2.setTypeface(font);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity2.setText(String.format("โปรตีน %.0f%%", (percentComplete/100)*proteinPercent*100f));
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
        textActivity3.setTypeface(font);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity3.setText(String.format("คาร์โบไฮเดรต %.0f%%", currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        carboIndex = mDecoView.addSeries(seriesItem);
    }

    private void createEvents(int cal, float protein, float fat, float carbo) {
        Log.d("Para CreateEvents","Fat "+fat+" Pro "+protein+" Carb "+carbo);
        float persentFat = fat+carbo+protein;
        float persentPro = protein+carbo;
        float persentCarb = carbo;
        Log.d("CreateEvents", "Fat " + persentFat + " Pro " + persentPro + " Carb " + persentCarb);
        mDecoView.executeReset();
//        mDecoView.deleteAll();
        mDecoView.addEvent(new DecoEvent.Builder(mSeriesMax)
                .setIndex(mBackIndex)
                .setDuration(3000)
                .setDelay(100)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(fatIndex)
                .setDuration(1600)
                .setDelay(1250)
                .build());
        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(proteinIndex)
                .setDuration(1800)
                .setEffectRotations(1)
                .setDelay(1250)
                .build());
        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(carboIndex)
                .setDuration(2000)
                .setEffectRotations(2)
                .setDelay(1250)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(persentFat)
                .setIndex(fatIndex)
                .setDelay(3250)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(persentPro)
                .setIndex(proteinIndex)
                .setDelay(3500)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(persentCarb)
                .setIndex(carboIndex)
                .setDelay(3750).build());

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
