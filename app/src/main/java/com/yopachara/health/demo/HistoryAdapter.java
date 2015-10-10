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
import com.yopachara.health.demo.Service.HealthService;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by yopachara on 9/20/15 AD.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private ArrayList<HistoryModel.History> history;
    private Context mContext;
    RecyclerView recyclerView;
    private HistoryModel historyModel;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView mTextView;
        public TextView name;
        public TextView date;
        public TextView username;
        public TextView cal;
        public TextView pro;
        public TextView fat;
        public TextView carbo;
        public String id;
        private List<HistoryModel.History> history;
        private HistoryAdapter ha;
        String API = "http://pachara.me:3000";

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
            Log.d("History Short Click", getAdapterPosition() + view.toString());
            Toast.makeText(view.getContext(), "Short click position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View view) {
            Log.d("History Long Click", getPosition() + view.toString());
            Toast.makeText(view.getContext(), "Delete food history = " + name, Toast.LENGTH_SHORT).show();
            delHistory(id);
            return true;


        }
        private void delHistory(String id) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API).build();
            HealthService api = restAdapter.create(HealthService.class);

            api.delHistoryID(id, new Callback<HistoryModel>() {
                @Override
                public void success(HistoryModel historyModel, Response response) {
                    Log.d("Success", "Delete history");
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
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
        viewHolder.id = player.getId();
    }

    @Override
    public int getItemCount() {
        return history.size();
    }





}
