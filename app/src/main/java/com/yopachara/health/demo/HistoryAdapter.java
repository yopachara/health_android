package com.yopachara.health.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yopachara.health.demo.Model.HistoryModel;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yopachara on 9/20/15 AD.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistoryModel.History> history;
    private Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView mTextView;
        public TextView name;
        public TextView date;
        public TextView username;
        public TextView cal;
        public TextView pro;
        public TextView fat;
        public TextView carbo;

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);
            username = (TextView) view.findViewById(R.id.username);
            cal = (TextView) view.findViewById(R.id.cal);
            pro = (TextView) view.findViewById(R.id.pro);
            fat = (TextView) view.findViewById(R.id.fat);
            carbo = (TextView) view.findViewById(R.id.carbo);


        }

        @Override
        public void onClick(View view) {
            Log.d("History Short Click", getPosition()+view.toString());
            Toast.makeText(view.getContext(), "Short click position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View view) {
            Log.d("History Long Click", getPosition()+view.toString());
            Toast.makeText(view.getContext(), "Long click position = " + getPosition(), Toast.LENGTH_SHORT).show();
            return true;
        }


    }

    public HistoryAdapter(Context context, ArrayList<HistoryModel.History> dataset) {
        history = dataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_history_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        HistoryModel.History player = history.get(position);

        viewHolder.name.setText(player.getFoodname());
        viewHolder.date.setText(player.getDate());
        viewHolder.username.setText(player.getUsername());
        viewHolder.cal.setText(player.getCal());
        viewHolder.fat.setText(player.getFat());
        viewHolder.carbo.setText(player.getCarbo());
        viewHolder.pro.setText(player.getProtein());

    }

    @Override
    public int getItemCount() {
        return history.size();
    }


}
