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
    float proteinExpect;
    float fatExpect;
    float carboExpect;
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
        proteinExpect = mUser.getProtein();
        fatExpect = mUser.getFat();
        carboExpect = mUser.getCarbo();
        font = Typeface.createFromAsset(getContext().getAssets(), "supermarket.ttf");
//        font = Typeface.createFromAsset(getContext().getAssets(), "Sukhumvit-Medium.ttf");
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
        resetText(getView(),"all");
        if(histories.size()==0){
            Log.d("Success", "today is no food");
            textActivity1.setText("Today is no food");
            resetText(getView(),"all");
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

    private void createDataSeries1(final View v) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textPercentage = (TextView) v.findViewById(R.id.fatText);
        final TextView fatPercentage = (TextView) v.findViewById(R.id.fatPercent);
        textPercentage.setTypeface(font);
        fatPercentage.setTypeface(font);
        textPercentage.setText("ไขมัน");
        final View fat1 = (View)v.findViewById(R.id.fat1);
        final View fat2 = (View)v.findViewById(R.id.fat2);
        final View fat3 = (View)v.findViewById(R.id.fat3);
        final View fat4 = (View)v.findViewById(R.id.fat4);
        final View fat5 = (View)v.findViewById(R.id.fat5);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = (percentComplete/100)*(((fatPercent*100f)/fatExpect)*100);
                fatPercentage.setText(String.format("%.0f%%", percentFilled));
                resetText(v,"fat");

                if(percentFilled>=20) {
                    fat1.setBackgroundResource(R.drawable.circle_orange);
                }
                if(percentFilled>=40) {
                    fat2.setBackgroundResource(R.drawable.circle_orange);
                }
                if(percentFilled>=60) {
                    fat3.setBackgroundResource(R.drawable.circle_orange);
                }
                if(percentFilled>=80) {
                    fat4.setBackgroundResource(R.drawable.circle_orange);
                }
                if(percentFilled>=100) {
                    fat5.setBackgroundResource(R.drawable.circle_orange);
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
                textToGo.setText(String.format("%.0f เหลือแคลอรี่", ((seriesItem.getMaxValue() - currentPosition)/100)*bmr));

            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        textActivity1 = (TextView) v.findViewById(R.id.textValue);
        final TextView textCal = (TextView) v.findViewById(R.id.calText);
        final TextView percenCal = (TextView) v.findViewById(R.id.calPercent);

        final View cal1 = (View)v.findViewById(R.id.cal1);
        final View cal2 = (View)v.findViewById(R.id.cal2);
        final View cal3 = (View)v.findViewById(R.id.cal3);
        final View cal4 = (View)v.findViewById(R.id.cal4);
        final View cal5 = (View)v.findViewById(R.id.cal5);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity1.setText(String.format("%.0f แคลอรี่", (currentPosition/100)*bmr));
                percenCal.setText(String.format("%.0f%%", currentPosition));
                textActivity1.setTypeface(font);
                textCal.setTypeface(font);
                percenCal.setTypeface(font);

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

    private void createDataSeries2(final View v) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF4444"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textActivity2 = (TextView) v.findViewById(R.id.proteinText);
        final TextView proteinPer = (TextView) v.findViewById(R.id.proteinPercent);
        textActivity2.setTypeface(font);
        proteinPer.setTypeface(font);

        final View pro1 = (View)v.findViewById(R.id.protein1);
        final View pro2 = (View)v.findViewById(R.id.protein2);
        final View pro3 = (View)v.findViewById(R.id.protein3);
        final View pro4 = (View)v.findViewById(R.id.protein4);
        final View pro5 = (View)v.findViewById(R.id.protein5);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = (percentComplete/100)*(((proteinPercent*100f)/proteinExpect)*100);
                proteinPer.setText(String.format("%.0f%%", percentFilled));
                textActivity2.setText("โปรตีน");

                resetText(v,"pro");

                if(percentFilled>=20) {
                    pro1.setBackgroundResource(R.drawable.circle_red);
                }
                if(percentFilled>=40) {
                    pro2.setBackgroundResource(R.drawable.circle_red);
                }
                if(percentFilled>=60) {
                    pro3.setBackgroundResource(R.drawable.circle_red);
                }
                if(percentFilled>=80) {
                    pro4.setBackgroundResource(R.drawable.circle_red);
                }
                if(percentFilled>=100) {
                    pro5.setBackgroundResource(R.drawable.circle_red);
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        proteinIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries3(final View v) {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FF6699FF"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textActivity3 = (TextView) v.findViewById(R.id.carboText);
        final TextView carboPer = (TextView) v.findViewById(R.id.carboPercent);

        textActivity3.setTypeface(font);
        carboPer.setTypeface(font);

        final View car1 = (View)v.findViewById(R.id.carbo1);
        final View car2 = (View)v.findViewById(R.id.carbo2);
        final View car3 = (View)v.findViewById(R.id.carbo3);
        final View car4 = (View)v.findViewById(R.id.carbo4);
        final View car5 = (View)v.findViewById(R.id.carbo5);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = (percentComplete/100)*(((carboPercent*100f)/carboExpect)*100);
                carboPer.setText(String.format("%.0f%%", percentFilled));
                resetText(v,"car");
                if(percentFilled>=20) {
                    car1.setBackgroundResource(R.drawable.circle_blue);
                }
                if(percentFilled>=40) {
                    car2.setBackgroundResource(R.drawable.circle_blue);
                }
                if(percentFilled>=60) {
                    car3.setBackgroundResource(R.drawable.circle_blue);
                }
                if(percentFilled>=80) {
                    car4.setBackgroundResource(R.drawable.circle_blue);
                }
                if(percentFilled>=100) {
                    car5.setBackgroundResource(R.drawable.circle_blue);
                }
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

    }

    private void resetText(View v, String text) {
        int circle = R.drawable.circle;

        final View cal1 = v.findViewById(R.id.cal1);
        final View cal2 = v.findViewById(R.id.cal2);
        final View cal3 = v.findViewById(R.id.cal3);
        final View cal4 = v.findViewById(R.id.cal4);
        final View cal5 = v.findViewById(R.id.cal5);
        final View fat1 = v.findViewById(R.id.fat1);
        final View fat2 = v.findViewById(R.id.fat2);
        final View fat3 = v.findViewById(R.id.fat3);
        final View fat4 = v.findViewById(R.id.fat4);
        final View fat5 = v.findViewById(R.id.fat5);
        final View pro1 = v.findViewById(R.id.protein1);
        final View pro2 = v.findViewById(R.id.protein2);
        final View pro3 = v.findViewById(R.id.protein3);
        final View pro4 = v.findViewById(R.id.protein4);
        final View pro5 = v.findViewById(R.id.protein5);
        final View car1 = v.findViewById(R.id.carbo1);
        final View car2 = v.findViewById(R.id.carbo2);
        final View car3 = v.findViewById(R.id.carbo3);
        final View car4 = v.findViewById(R.id.carbo4);
        final View car5 = v.findViewById(R.id.carbo5);

        switch (text){
            case "cal":
                cal1.setBackgroundResource(circle);
                cal2.setBackgroundResource(circle);
                cal3.setBackgroundResource(circle);
                cal4.setBackgroundResource(circle);
                cal5.setBackgroundResource(circle);
                break;
            case "fat":
                fat1.setBackgroundResource(circle);
                fat2.setBackgroundResource(circle);
                fat3.setBackgroundResource(circle);
                fat4.setBackgroundResource(circle);
                fat5.setBackgroundResource(circle);
                break;
            case "pro":
                pro1.setBackgroundResource(circle);
                pro2.setBackgroundResource(circle);
                pro3.setBackgroundResource(circle);
                pro4.setBackgroundResource(circle);
                pro5.setBackgroundResource(circle);
                break;
            case "car":
                car1.setBackgroundResource(circle);
                car2.setBackgroundResource(circle);
                car3.setBackgroundResource(circle);
                car4.setBackgroundResource(circle);
                car5.setBackgroundResource(circle);
                break;
            case "all":
                cal1.setBackgroundResource(circle);
                cal2.setBackgroundResource(circle);
                cal3.setBackgroundResource(circle);
                cal4.setBackgroundResource(circle);
                cal5.setBackgroundResource(circle);
                fat1.setBackgroundResource(circle);
                fat2.setBackgroundResource(circle);
                fat3.setBackgroundResource(circle);
                fat4.setBackgroundResource(circle);
                fat5.setBackgroundResource(circle);
                pro1.setBackgroundResource(circle);
                pro2.setBackgroundResource(circle);
                pro3.setBackgroundResource(circle);
                pro4.setBackgroundResource(circle);
                pro5.setBackgroundResource(circle);
                car1.setBackgroundResource(circle);
                car2.setBackgroundResource(circle);
                car3.setBackgroundResource(circle);
                car4.setBackgroundResource(circle);
                car5.setBackgroundResource(circle);
        }
    }


}
