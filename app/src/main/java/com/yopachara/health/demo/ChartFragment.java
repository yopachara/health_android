package com.yopachara.health.demo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

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

import org.joda.time.DateTime;

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

    private LineChart mChartAll;
    private LineChart mChartWeek;
    private LineChart mChartMonth;
    private CustomViewPager vp;
    private HistoryAdapter mAdapter;
    private HistoryModel historyModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
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
        vp=(CustomViewPager) getActivity().findViewById(R.id.main_vp);

        setHasOptionsMenu(true);
        ScrollView mScrollView = (ScrollView) v.findViewById(R.id.mScrollView);
        mChartAll = (LineChart) v.findViewById(R.id.chart1);
        mChartWeek = (LineChart) v.findViewById(R.id.chartWeek);
        mChartMonth = (LineChart) v.findViewById(R.id.chartMonth);
        mScrollView.setEnabled(false);
        mChartAll.setDescription("ช่วงเวลาทั้งหมด");
        mChartAll.setNoDataTextDescription("โปรดใส่ข้อมูลเพื่อพล็อตกราฟ.");
        mChartWeek.setDescription("อาทิตย์ที่ผ่านมา");
        mChartWeek.setNoDataTextDescription("โปรดใส่ข้อมูลเพื่อพล็อตกราฟ.");
        mChartMonth.setDescription("เดือนที่ผ่านมา");
        mChartMonth.setNoDataTextDescription("โปรดใส่ข้อมูลเพื่อพล็อตกราฟ.");

        setupChart(mChartAll, v);
        setupChart(mChartWeek, v);
        setupChart(mChartMonth, v);

        getHistory(v);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeChartContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHistory(v);
            }
        });


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

    private void setData(int count, float range, ArrayList<Entry> chart, ArrayList<String> index, LineChart mChart) {

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
        LineDataSet set1 = new LineDataSet(chart, "แคลอรี่");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleSize(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

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

                final HashMap<DateTime, Integer> classes = new HashMap<DateTime, Integer>();

                for (int i = 0; i < history.size() - 1; i++) {
                    Log.d("Date", history.get(i).getDate());

                    String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                    String patternShort = "yyyy-MM-dd";
                    SimpleDateFormat format = new SimpleDateFormat(pattern);
                    SimpleDateFormat formatShort = new SimpleDateFormat(patternShort);

                    try {
                        DateTime date = new DateTime(format.parse(history.get(i).getDate()));
                        Log.d("Day", date.getDayOfMonth() + "");
                        DateTime x = new DateTime(formatShort.parse(date.getYear() + "-" + date.getMonthOfYear() + "-" + date.getDayOfMonth()));
                        int cal = history.get(i).getCalInt();

                        if (!classes.containsKey(x)) {
                            classes.put(x, cal);
                        } else {
                            classes.put(x, classes.get(x) + cal);
                        }


                    } catch (java.text.ParseException e) {
                        //handle exception
                        Log.d("Error ", e.toString());
                    }

                    totalcal = totalcal + history.get(i).getCalInt();
                }
                ArrayList<Entry> chartAll = new ArrayList<Entry>();
                ArrayList<String> indexAll = new ArrayList<String>();

                ArrayList<Entry> chartMonth = new ArrayList<Entry>();
                ArrayList<String> indexMonth = new ArrayList<String>();

                ArrayList<Entry> chartWeek = new ArrayList<Entry>();
                ArrayList<String> indexWeek = new ArrayList<String>();

                int count = 0;
                //Map<Integer,Integer> s = sortByValue(classes);
                Map<DateTime, Integer> treeMap = new TreeMap<DateTime, Integer>(classes);
                DateTime lastWeek = new DateTime().minusDays(7);
                DateTime lastMonth = new DateTime().minusDays(30);
                Log.d("Last Week", lastWeek + " " + lastWeek.getDayOfMonth() + "");

                for (Map.Entry<DateTime, Integer> entry : treeMap.entrySet()) {
                    Log.d("Day " + entry.getKey(), " total " + entry.getValue() + " cals.");
                    indexAll.add(entry.getKey().getDayOfMonth() + "");
                    chartAll.add(new Entry(entry.getValue(), count));
                    if (lastWeek.isBefore(entry.getKey())) {
                        indexWeek.add(entry.getKey().getDayOfMonth() + "");
                        chartWeek.add(new Entry(entry.getValue(), count));
                    }
                    if (lastMonth.isBefore(entry.getKey())) {
                        indexMonth.add(entry.getKey().getDayOfMonth() + "");
                        chartMonth.add(new Entry(entry.getValue(), count));
                    }
                    count++;
                }
                Log.d("Size of month", indexMonth.size() + "");
                Log.d("Size of week", indexWeek.size() + "");
                for (int i = 0; i < indexMonth.size(); i++) {
                    int dayMonth = lastMonth.plusDays(i + 1).getDayOfMonth();
                    if (!indexMonth.contains(dayMonth + "")) {
                        indexMonth.add(i, dayMonth + "");
                        chartMonth.add(i, new Entry(0, i));
                    } else {
                        float val = chartMonth.get(i).getVal();
                        chartMonth.set(i, new Entry(val, i));
                    }
                }
                for (int i = 0; i < indexWeek.size(); i++) {
                    int dayWeek = lastWeek.plusDays(i + 1).getDayOfMonth();
                    Log.d("dayWeek", dayWeek + "");
                    if (!indexWeek.contains(dayWeek + "")) {
                        Log.d("Day is not contains", dayWeek + "");
                        indexWeek.add(i, dayWeek + "");
                        chartWeek.add(i, new Entry(0, i));
                    } else {
                        float val = chartWeek.get(i).getVal();
                        chartWeek.set(i, new Entry(val, i));
                    }
                }
                for (int i = 0; i < indexAll.size(); i++) {
                    Log.d("Index All", indexAll.get(i));
                    Log.d("Chart All", chartAll.get(i) + "");
                }

                for (int i = 0; i < indexMonth.size(); i++) {
                    Log.d("Index Month", indexMonth.get(i));
                    Log.d("Chart Month", chartMonth.get(i) + "");
                }
                for (int i = 0; i < indexWeek.size(); i++) {
                    Log.d("Index Week", indexWeek.get(i));
                    Log.d("Chart Week", chartWeek.get(i) + "");
                }
                Log.d("Size of month", indexMonth.size() + "");
                Log.d("Size of week", indexWeek.size() + "");

                int size = classes.size();
                setData(size, size, chartAll, indexAll, mChartAll);
                setData(indexWeek.size(), indexWeek.size(), chartWeek, indexWeek, mChartWeek);
                setData(indexMonth.size(), indexMonth.size(), chartMonth, indexMonth, mChartMonth);

                Log.d("Size", size + "");
                Log.d("TOTALCAL", totalcal + "");
                mSwipeRefreshLayout.setRefreshing(false);

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
        inflater.inflate(R.menu.menu_chart, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem lock = menu.findItem(R.id.locker);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Item", item.getItemId() + "");
        if (vp.getPagingEnabled()) {
            vp.setPagingEnabled(false);
            item.setIcon(R.drawable.ic_action_name);
        } else {
            vp.setPagingEnabled(true);
            item.setIcon(R.drawable.ic_unlock);
        }
        return false;
    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByKey(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private void setupChart(LineChart mChart, View v) {

        mChart.setOnChartValueSelectedListener(this);

        // no description text



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
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaxValue(2500);
        leftAxis.setDrawGridLines(false);

    }

}
