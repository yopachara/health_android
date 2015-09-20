package com.yopachara.health.demo;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yopachara.health.demo.Model.FoodModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yopachara on 9/19/15 AD.
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<FoodModel.Foods> mPlayers;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public TextView type;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            name = (TextView) view.findViewById(R.id.name);
            type = (TextView) view.findViewById(R.id.club);
        }
        @Override
        public void onClick(View view) {
            Snackbar.make(view.getRootView(),"position = " + getPosition()+" "+name.getText(), Snackbar.LENGTH_LONG).show();
        }
    }

    public FoodAdapter(Context context, ArrayList<FoodModel.Foods> dataset) {
        mPlayers = dataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_view_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        FoodModel.Foods player = mPlayers.get(position);

        viewHolder.name.setText(player.getName());
        viewHolder.type.setText(player.getType());
    }

    @Override
    public int getItemCount() {
        return mPlayers.size();
    }
}
