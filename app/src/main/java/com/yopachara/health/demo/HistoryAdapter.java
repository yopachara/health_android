package com.yopachara.health.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yopachara.health.demo.Model.HistoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yopachara on 9/20/15 AD.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryModel.History> mPlayers;
    private Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public TextView name;
        public TextView date;
        public TextView username;

        private ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);


            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.club);
            username = (TextView) view.findViewById(R.id.username);

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }




//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView name;
//        public TextView date;
//        public TextView username;
//
//        public ViewHolder(View view) {
//            super(view);
//
//            name = (TextView) view.findViewById(R.id.name);
//            date = (TextView) view.findViewById(R.id.club);
//            username = (TextView) view.findViewById(R.id.username);
//        }
//    }

    public HistoryAdapter(Context context, ArrayList<HistoryModel.History> dataset) {
        mPlayers = dataset;
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
        HistoryModel.History player = mPlayers.get(position);

        viewHolder.name.setText(player.getName());
        viewHolder.date.setText(player.getDate());
        viewHolder.username.setText(player.getUsername());

    }

    @Override
    public int getItemCount() {
        return mPlayers.size();
    }


}
