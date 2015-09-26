package com.yopachara.health.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;


public class HomeFragment extends Fragment {

    protected boolean mUpdateListeners = true;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        DecoView arcView = (DecoView)v.findViewById(R.id.dynamicArcView);

        // Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .build());

        //Create data series track
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        int series1Index = arcView.addSeries(seriesItem1);
        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(1000)
                .setDuration(2000)
                .build());

        final TextView textPercent = (TextView) v.findViewById(R.id.textValue);
        if (textPercent != null) {
            textPercent.setText("");
            addProgressListener(seriesItem1, textPercent, "%.0f%%");
        }

        arcView.addEvent(new DecoEvent.Builder(25).setIndex(series1Index).setDelay(4000).build());
        arcView.addEvent(new DecoEvent.Builder(100).setIndex(series1Index).setDelay(10000).build());
        arcView.addEvent(new DecoEvent.Builder(14).setIndex(series1Index).setDelay(15000).build());


        return v;
    }



    protected void addProgressListener(@NonNull final SeriesItem seriesItem, @NonNull final TextView view, @NonNull final String format) {
        if (format.length() <= 0) {
            throw new IllegalArgumentException("String formatter can not be empty");
        }

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (mUpdateListeners) {
                    if (format.contains("%%")) {
                        // We found a percentage so we insert a percentage
                        float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                        view.setText(String.format(format, percentFilled * 100f));
                    } else {
                        view.setText(String.format(format, currentPosition));
                    }
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });
    }


}
