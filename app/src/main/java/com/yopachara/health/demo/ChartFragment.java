package com.yopachara.health.demo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.rey.material.widget.SnackBar;
import com.yopachara.health.demo.Model.HistoryModel;
import com.yopachara.health.demo.Service.HealthService;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ChartFragment extends Fragment implements OnChartValueSelectedListener {

    private LineChart mChart;
    private HistoryAdapter mAdapter;
    private HistoryModel historyModel;
    String API = "http://pachara.me:3000";
    SnackBar mSnackBar;
    int totalcal = 0;

    public static ChartFragment newInstance() {
        ChartFragment fragment = new ChartFragment();

        return fragment;
    }

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_chart, container, false);


        mChart = (LineChart) v.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        // add data
//        setData(20, 30);

        mChart.animateX(2500);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "supermarket.ttf");

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(tf);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(tf);
        rightAxis.setTextColor(ColorTemplate.getHoloBlue());
        rightAxis.setAxisMaxValue(2500f);
        rightAxis.setDrawGridLines(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.RED);
        leftAxis.setAxisMaxValue(2500);
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMinValue(-200);
        leftAxis.setDrawGridLines(false);

        getHistory(v);

        return v;
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    private void setData(int count, float range, ArrayList<Entry> chart, ArrayList<String> index) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((index.get(i)) + "");
        }

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 50;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(chart, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleSize(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        //set1.setFillFormatter(new MyFillFormatter(0f));
//        set1.setDrawHorizontalHighlightIndicator(false);
//        set1.setVisible(false);
//        set1.setCircleHoleColor(Color.WHITE);

//        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
//
//        for (int i = 0; i < count; i++) {
//            float mult = range;
//            float val = (float) (Math.random() * mult) + 450;// + (float)
//            // ((mult *
//            // 0.1) / 10);
//            yVals2.add(new Entry(val, i));
//        }
//
//        // create a dataset and give it a type
//        LineDataSet set2 = new LineDataSet(yVals2, "DataSet 2");
//        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
//        set2.setColor(Color.RED);
//        set2.setCircleColor(Color.WHITE);
//        set2.setLineWidth(2f);
//        set2.setCircleSize(3f);
//        set2.setFillAlpha(65);
//        set2.setFillColor(Color.RED);
//        set2.setDrawCircleHole(false);
//        set2.setHighLightColor(Color.rgb(244, 117, 117));
//        //set2.setFillFormatter(new MyFillFormatter(900f));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
//        dataSets.add(set2);
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }

    public void getHistory(final View v) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API).build();
        HealthService api = restAdapter.create(HealthService.class);
        api.getHistorys(new Callback<HistoryModel>() {
            @Override
            public void success(HistoryModel historyModel, Response response) {
                ArrayList<HistoryModel.History> history = historyModel.getObjects();

                Log.d("Success", "Chart size " + history.size());

                final HashMap<Date, Integer> classes = new HashMap<Date, Integer>();

                for(int i = 0 ;i < history.size()-1;i++){
                    Log.d("Date",history.get(i).getDate());

                    String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                    String patternShort = "yyyy-MM-dd";
                    SimpleDateFormat format = new SimpleDateFormat(pattern);
                    SimpleDateFormat formatShort = new SimpleDateFormat(patternShort);

                    try {
                        Date date = format.parse(history.get(i).getDate());
                        Log.d("Day", date.getDate() + "");
                        Date x = formatShort.parse(date.getYear()+"-"+date.getMonth()+"-"+date.getDate());
                        int cal = Integer.parseInt(history.get(i).getCal());

                        if (!classes.containsKey(x)) {
                            classes.put(x,cal );
                        } else {
                            classes.put(x, classes.get(x) + cal);
                        }


                    } catch (java.text.ParseException e) {
                        //handle exception
                        Log.d("Error ",e.toString());
                    }

                    totalcal = totalcal+ Integer.parseInt(history.get(i).getCal());
                }
                ArrayList<Entry> chart = new ArrayList<Entry>();
                ArrayList<String> index = new ArrayList<String>();
                int count = 0;
                //Map<Integer,Integer> s = sortByValue(classes);
                Map<Date, Integer> treeMap = new TreeMap<Date, Integer>(classes);

                for (Map.Entry<Date, Integer> entry : treeMap.entrySet()) {
                    Log.d("Day " + entry.getKey(), " total " + entry.getValue() + " cals.");
                    index.add(entry.getKey().getDate()+"/"+entry.getKey().getMonth());
                    chart.add(new Entry(entry.getValue(),count));
                    count++;

                }
                int size = classes.size();
                setData(size,size,chart,index);
                Log.d("Dict",classes.get(21)+"");
                Log.d("TOTALCAL", totalcal+"");
            }

            @Override
            public void failure(RetrofitError error) {

                Log.d("Error", error.toString());
            }

        });
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByKey( Map<K, V> map ) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

}
